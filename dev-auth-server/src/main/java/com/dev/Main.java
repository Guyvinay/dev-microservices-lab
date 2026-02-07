package com.dev;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

record Employee(Long id, String name, String department, double salary) {}

public class Main {
    public static void main(String[] args) {

        groupingByFunction();

    }

    private static void groupingByFunction() {


        List<Employee> employees = List.of(
                new Employee(1L, "Alice", "IT", 90000),
                new Employee(2L, "Bob", "HR", 60000),
                new Employee(3L, "Charlie", "IT", 95000),
                new Employee(4L, "David", "Finance", 80000)
        );

        Map<String, List<Employee>> empMap = employees.stream().collect(
                Collectors.groupingBy(
                        Employee::department
                )
        );

        Map<String, Long> countMap = employees.stream().collect(
                Collectors.groupingBy(
                        Employee::department,
                        Collectors.counting()
                )
        );



        System.out.println(countMap);
    }
}