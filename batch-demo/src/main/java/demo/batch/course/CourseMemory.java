package demo.batch.course;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.net.InetAddress;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.input.AutoCloseInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CourseMemory.
 */
class CourseMemory extends TimerTask {

    static final Logger memory = LoggerFactory.getLogger("demo.memory");

    public static void main(final String[] args) {
        final Timer timer = new Timer(false);
        timer.schedule(new CourseMemory(), 1000, 5000);
    }

    @Override
    public void run() {
        memory();
        memoryMap();
    }

    void memory() {
        final List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();

        for (final MemoryPoolMXBean pool : pools) {
            if (pool.getType() == MemoryType.NON_HEAP) {
                continue;
            }

            final String name = pool.getName();
            final MemoryUsage usage = pool.getUsage();

            final long used = usage.getUsed();
            final long max = usage.getMax();
            final float percent = 100f * used / max;

            memory.info(String.format("%-13s: %2.2f%% used=%d (%s), max=%d (%s)", name, percent,
                used, formated(used), max, formated(max)));
        }
    }

    void memoryMap() {
        final String javaHome = System.getenv("JAVA_HOME");
        final File jmap = new File(javaHome, "bin" + File.separator + "jmap");
        if (!jmap.exists()) return;

        java.lang.Process process = null;

        try {
            final String hostName = InetAddress.getLocalHost().getHostName();
            final String runtimeName = ManagementFactory.getRuntimeMXBean().getName();
            if (!runtimeName.matches("\\d+@" + hostName)) return;

            // pid
            final String pid = runtimeName.substring(0, runtimeName.indexOf('@'));

            // exec: jmap -histo:live pid
            process = Runtime.getRuntime().exec(
                new String[] { jmap.getAbsolutePath(), "-histo:live", pid });

            // read jmap stdout
            final BufferedReader reader = new BufferedReader(new InputStreamReader(
                new AutoCloseInputStream(process.getInputStream())));

            // jmap output pattern
            final Pattern pattern = Pattern.compile("\\s+\\d+:\\s+\\d+\\s+\\d+\\s+(\\p{Graph}+)");

            try {
                for (int lines = 0; lines < 20;) {
                    final String line = reader.readLine();

                    if (line == null) {
                        break;
                    }

                    final Matcher matcher = pattern.matcher(line);

                    if (matcher.matches()) {
                        final String group = matcher.group(1);

                        // exclude some meaningless line
                        if ("[[I".equals(group) || "[J".equals(group) || "[C".equals(group)
                            || "[I".equals(group) || "[B".equals(group) || "[S".equals(group)
                            || group.startsWith("<")) {
                            continue;
                        }

                        lines++;
                        memory.info(line);
                    }
                }

            } catch (final Exception e) {
                e.printStackTrace();

            } finally {
                if (reader != null) {
                    reader.close();
                }
            }

        } catch (final Exception e) {
            e.printStackTrace();

        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    // private static final String HOTSPOT_DIAGNOSTIC = "com.sun.management:type=HotSpotDiagnostic";
    //
    // @SuppressWarnings("restriction")
    // void heapdump() {
    // try {
    // final HotSpotDiagnosticMXBean diagnosticMXBean = ManagementFactory
    // .newPlatformMXBeanProxy(ManagementFactory.getPlatformMBeanServer(),
    // HOTSPOT_DIAGNOSTIC, HotSpotDiagnosticMXBean.class);
    //
    // diagnosticMXBean.dumpHeap("course.bin", true);
    //
    // } catch (final Exception e) {
    // e.printStackTrace();
    // }
    // }

    static String formated(final long bytes) {
        final float ko = bytes / 1024f;

        if (ko > 1024f * 102.4) {
            final float mo = ko / 1024f;
            return String.format("%,.1f MB", mo);

        } else return String.format("%,.1f KB", ko);
    }
}
