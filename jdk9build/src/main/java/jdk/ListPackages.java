/*
 * Copyright (c) 2016, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package jdk;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * Lists the packages of the given JAR file or exploded directory
 * and reports the list of split packages
 */
public class ListPackages {
    public static void main(String... args) throws IOException {
        if (args.length == 0) {
            System.out.println("ListPackages <list.txt | jarfile | exploded directory> ...");
            System.exit(1);
        }
        List<ListPackages> analyzers = new ArrayList<>();

        for (String arg : args) {
            Path p = Paths.get(arg);
            if (!addAnalyzer(analyzers, p)) {
                if (arg.equals("list.txt")) {
                    Files.lines(p).forEach(line -> {
                        Path lp = Paths.get(line);
                        if (!addAnalyzer(analyzers, lp)) {
                            throw new IllegalArgumentException(lp.toString() +
                                " not a directory and not a JAR file");
                        }
                    });
                } else {
                    throw new IllegalArgumentException(p.toString() +
                        " not a directory and not a JAR file");
                }
            }
        }

        Map<String, ListPackages> packageToModule = packageToModule();

        Map<String, List<ListPackages>> pkgs = new HashMap<>();
        for (ListPackages analyzer : analyzers) {
            analyzer.packages().stream()
                    .forEach(pn -> {
                        List<ListPackages> values =
                            pkgs.computeIfAbsent(pn, _k -> new ArrayList<>());
                        values.add(analyzer);
                        if (packageToModule.containsKey(pn)) {
                            values.add(packageToModule.get(pn));
                        }
                    });
        }

        System.out.println("Split packages: ");
        pkgs.entrySet().stream()
            .filter(e -> e.getValue().size() > 1)
            .sorted(Map.Entry.comparingByKey())
            .forEach(e -> {
                System.out.println(e.getKey());
                e.getValue().stream()
                    .map(ListPackages::location)
                    .forEach(location -> System.out.format("    %s%n", location));
            });

        System.out.println("All packages: ");
        for (ListPackages analyzer : analyzers) {
            System.out.println(analyzer.location());
            analyzer.packages.stream()
                .sorted()
                .forEach(p -> System.out.format("   %s%n", p));
        }
    }

    private static final String MODULE_INFO = "module-info.class";

    private final URI location;
    private final Set<String> packages;
    private ListPackages(Path path, Supplier<Set<String>> supplier) throws IOException {
        this.location = path.toUri();
        this.packages = supplier.get();
    }

    private ListPackages(ModuleReference mref) {
        this.location = mref.location().get();
        this.packages = mref.descriptor().packages();
    }

    Set<String> packages() {
        return packages;
    }

    URI location() {
        return location;
    }

    /**
     * Adds an analyzer for the given resource if it's a directory or jar file.
     *
     * @throws IllegalArgumentException if the resource does not exist
     */
    private static boolean addAnalyzer(List<ListPackages> analyzers, Path resource) {
        if (!Files.exists(resource)) {
            throw new IllegalArgumentException(resource.toString());
        }
        try {
            if (Files.isDirectory(resource)) {
                analyzers.add(new ListPackages(resource, () -> packages(resource)));
                return true;
            } else if (String.valueOf(resource.getFileName()).endsWith(".jar")) {
                analyzers.add(new ListPackages(resource, () -> jarFilePackages(resource)));
                return true;
            }
            return false;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Walks the given directory and returns all packages.
     *
     * This method needs to be updated to include resources
     * for #ResourceEncapsulation.
     */
    private static Set<String> packages(Path dir) {
        try {
            return Files.find(dir, Integer.MAX_VALUE,
                (p, attr) -> p.getFileName().toString().endsWith(".class") &&
                    !p.getFileName().toString().equals(MODULE_INFO))
                .map(Path::getParent)
                .map(dir::relativize)
                .map(Path::toString)
                .map(p -> p.replace(File.separator, "."))
                .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Returns all packages of the given JAR file.
     */
    private static Set<String> jarFilePackages(Path path) {
        try (JarFile jf = new JarFile(path.toFile())) {
            return jf.stream()
                .map(JarEntry::getName)
                .filter(n -> n.endsWith(".class") && !n.equals(MODULE_INFO))
                .map(ListPackages::toPackage)
                .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String toPackage(String name) {
        int i = name.lastIndexOf('/');
        return i != -1 ? name.substring(0, i).replace("/", ".") : "";
    }

    private static Map<String, ListPackages> packageToModule() {
        Map<String, ListPackages> map = new HashMap<>();
        ModuleFinder.ofSystem().findAll()
            .stream()
            .map(mref -> new ListPackages(mref))
            .forEach(o -> o.packages().forEach(pn -> map.put(pn, o)));
        return map;
    }

}
