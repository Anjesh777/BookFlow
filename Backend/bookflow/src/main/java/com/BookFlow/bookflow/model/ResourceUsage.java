package com.BookFlow.bookflow.model;


import lombok.Data;

@Data
public class ResourceUsage {
    private Storage storage;
    private Memory memory;
    private Cpu cpu;

    @Data
    public static class Storage {
        private long used;
        private long total;
        private double percentage;
    }

    @Data
    public static class Memory {
        private long used;
        private long total;
        private double percentage;
    }

    @Data
    public static class Cpu {
        private double percentage;
    }
}