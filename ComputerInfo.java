import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ComputerInfo {

    public static void main(String[] args) {
        // Display initial CPU and system information
        displaySystemInfo();

        // Stress the CPU to get max output
        System.out.println("\nIncreasing CPU usage to maximize output...");
        increaseCPUUsage();

        // Display CPU and system information after stress test
        System.out.println("\nCPU and system information after maximizing output:");
        displaySystemInfo();
    }

    private static void displaySystemInfo() {
        // Get the standard OperatingSystemMXBean instance
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();

        // Display basic information available from the standard OperatingSystemMXBean
        System.out.println("OS Architecture: " + osBean.getArch());
        System.out.println("Number of Logical Processors: " + osBean.getAvailableProcessors());
        System.out.println("System Load Average: " + osBean.getSystemLoadAverage());

        // Try to cast to com.sun.management.OperatingSystemMXBean for more detailed information
        if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
            com.sun.management.OperatingSystemMXBean sunOsBean = (com.sun.management.OperatingSystemMXBean) osBean;

            // Display additional CPU and memory information
            System.out.println("System CPU Load: " + (sunOsBean.getSystemCpuLoad() * 100) + "%");
            System.out.println("Process CPU Load: " + (sunOsBean.getProcessCpuLoad() * 100) + "%");
            System.out.println("Total Physical Memory: " + formatSize(sunOsBean.getTotalPhysicalMemorySize()));
            System.out.println("Free Physical Memory: " + formatSize(sunOsBean.getFreePhysicalMemorySize()));
            System.out.println("Committed Virtual Memory: " + formatSize(sunOsBean.getCommittedVirtualMemorySize()));
        } else {
            System.out.println("Detailed CPU and memory information not available on this system.");
        }

        // Retrieve JVM memory usage information
        Runtime runtime = Runtime.getRuntime();
        System.out.println("JVM Memory (Max): " + formatSize(runtime.maxMemory()));
        System.out.println("JVM Memory (Total): " + formatSize(runtime.totalMemory()));
        System.out.println("JVM Memory (Free): " + formatSize(runtime.freeMemory()));

        // Retrieve system properties
        Properties props = System.getProperties();
        System.out.println("Java Version: " + props.getProperty("java.version"));
        System.out.println("Java Vendor: " + props.getProperty("java.vendor"));
        System.out.println("OS Name: " + props.getProperty("os.name"));
        System.out.println("OS Version: " + props.getProperty("os.version"));
        System.out.println("User Name: " + props.getProperty("user.name"));
        System.out.println("User Home Directory: " + props.getProperty("user.home"));
        System.out.println("User Working Directory: " + props.getProperty("user.dir"));
    }

    private static void increaseCPUUsage() {
        int cores = Runtime.getRuntime().availableProcessors();
        int threadCount = cores * 2; // Create more threads than cores to increase load
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        // Create CPU-intensive tasks
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                long startTime = System.nanoTime();
                while (System.nanoTime() - startTime < TimeUnit.SECONDS.toNanos(30)) { // Run for 30 seconds
                    // Busy loop to stress CPU
                    double value = Math.pow(Math.random(), Math.random());
                }
            });
        }

        executor.shutdown();
        try {
            // Wait until all tasks are finished
            if (!executor.awaitTermination(35, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    // Helper method to format sizes in bytes to a human-readable format
    private static String formatSize(long size) {
        double kb = size / 1024.0;
        double mb = kb / 1024.0;
        double gb = mb / 1024.0;
        if (gb >= 1) {
            return String.format("%.2f GB", gb);
        } else if (mb >= 1) {
            return String.format("%.2f MB", mb);
        } else if (kb >= 1) {
            return String.format("%.2f KB", kb);
        } else {
            return size + " B";
        }
    }
}
