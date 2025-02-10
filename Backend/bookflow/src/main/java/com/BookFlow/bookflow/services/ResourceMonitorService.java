package com.BookFlow.bookflow.services;

import com.BookFlow.bookflow.model.ResourceUsage;
import com.sun.management.OperatingSystemMXBean;
import org.springframework.stereotype.Service;

import java.io.File;
import java.lang.management.ManagementFactory;

@Service
public class ResourceMonitorService {

    public ResourceUsage getSystemResources() {
        ResourceUsage resourceUsage = new ResourceUsage();

        // CPU Usage
        ResourceUsage.Cpu cpu = new ResourceUsage.Cpu();
        OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        double cpuLoad = osBean.getCpuLoad() * 100;
        cpu.setPercentage(Math.round(cpuLoad * 100.0) / 100.0);

        // Get Memory Usage
        ResourceUsage.Memory memory = new ResourceUsage.Memory();
        long totalMemory = osBean.getTotalMemorySize();
        long usedMemory = totalMemory - osBean.getFreeMemorySize();
        memory.setTotal(totalMemory / (1024 * 1024 * 1024)); // Convert to GB
        memory.setUsed(usedMemory / (1024 * 1024 * 1024)); //
        memory.setPercentage(Math.round((usedMemory * 100.0) / totalMemory * 100.0) / 100.0);

        // Get Storage Usage
        ResourceUsage.Storage storage = new ResourceUsage.Storage();
        File root = new File("/");
        long totalSpace = root.getTotalSpace();
        long usableSpace = root.getUsableSpace();
        long usedSpace = totalSpace - usableSpace;
        storage.setTotal(totalSpace / (1024 * 1024 * 1024));
        storage.setUsed(usedSpace / (1024 * 1024 * 1024));
        storage.setPercentage(Math.round((usedSpace * 100.0) / totalSpace * 100.0) / 100.0);

        resourceUsage.setCpu(cpu);
        resourceUsage.setMemory(memory);
        resourceUsage.setStorage(storage);

        return resourceUsage;
    }
}
