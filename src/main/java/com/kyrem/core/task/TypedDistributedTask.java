package com.kyrem.core.task;

import lombok.Builder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Generic distributed task executor that spreads values across multiple buckets
 * for load-balanced processing. Implements {@link Runnable} so it can be
 * scheduled with {@link TaskManager#runRepeating}.
 *
 * <pre>{@code
 * TypedDistributedTask<Plant> task = new TypedDistributedTask<>(
 *     plant -> plant.tick(),
 *     plant -> plant.isFullyGrown(),
 *     4
 * );
 * taskManager.runRepeating(task, 0L, 1L);
 * }</pre>
 */
public class TypedDistributedTask<T> implements Runnable {

    private final Consumer<T> action;
    private final Predicate<T> escapeCondition;
    private final List<LinkedList<Supplier<T>>> suppliedValueMatrix;
    private final int distributionSize;
    private int currentPosition = 0;

    @Builder
    public TypedDistributedTask(Consumer<T> action, Predicate<T> escapeCondition, int distributionSize) {
        this.distributionSize = distributionSize;
        this.action = action;
        this.escapeCondition = escapeCondition;
        this.suppliedValueMatrix = new ArrayList<>(distributionSize);
        for (int i = 0; i < distributionSize; i++) {
            this.suppliedValueMatrix.add(new LinkedList<>());
        }
    }

    public void addValue(Supplier<T> valueSupplier) {
        if (valueSupplier == null) return;

        LinkedList<Supplier<T>> smallestList = this.suppliedValueMatrix.get(0);
        for (int index = 1; index < this.distributionSize; index++) {
            final LinkedList<Supplier<T>> candidate = this.suppliedValueMatrix.get(index);
            synchronized (candidate) {
                if (candidate.size() < smallestList.size()) {
                    smallestList = candidate;
                }
            }
        }

        synchronized (smallestList) {
            smallestList.add(valueSupplier);
        }
    }

    private void proceedPosition() {
        if (++this.currentPosition >= this.distributionSize) {
            this.currentPosition = 0;
        }
    }

    @Override
    public void run() {
        final LinkedList<Supplier<T>> currentList = this.suppliedValueMatrix.get(this.currentPosition);
        synchronized (currentList) {
            currentList.removeIf(this::executeThenCheck);
        }
        this.proceedPosition();
    }

    private boolean executeThenCheck(Supplier<T> valueSupplier) {
        if (valueSupplier == null) return true;

        T value;
        try {
            value = valueSupplier.get();
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }

        if (value == null) return true;

        try {
            this.action.accept(value);
            return this.escapeCondition.test(value);
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }
}
