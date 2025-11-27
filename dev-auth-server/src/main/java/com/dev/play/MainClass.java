package com.dev.play;

import com.dev.play.patterns.Abstract.Button;
import com.dev.play.patterns.Abstract.Checkbox;
import com.dev.play.patterns.Abstract.GUIFactory;
import com.dev.play.patterns.Abstract.WindowsFactory;
import com.dev.play.patterns.SingletonClass;
import com.dev.play.patterns.builder.UserBuilder;
import com.dev.play.patterns.factory.Shape;
import com.dev.play.patterns.factory.ShapeFactory;
import com.dev.play.patterns.prototype.Employee;

public class MainClass {

    public static void main(String[] args) {

        /**
         * Singleton pattern
         * Ensures only ONE instance of a class exists in the entire application, and provides a global way to access it.
         */
        SingletonClass singletonClass1 = SingletonClass.getInstance();
        SingletonClass singletonClass2 = SingletonClass.getInstance();

        System.out.println(singletonClass1 == singletonClass2);

        /**
         * Factory method.
         * Creates ONE object.
         * You ask the factory for an object → factory decides which concrete class to return.
         */
        // Ask factory for a specific shape
        Shape shape1 = ShapeFactory.getShape("circle");
        shape1.draw();   // Drawing a Circle

        Shape shape2 = ShapeFactory.getShape("square");
        shape2.draw();   // Drawing a Square


        /**
         * Abstract Factory
         * Creates a FAMILY of related objects.
         * You choose one factory → it gives you all matching objects (same theme/family).
         */
        GUIFactory guiFactory = new WindowsFactory();

        Button button = guiFactory.createButton(); // Windows button
        Checkbox checkbox = guiFactory.createCheckbox(); // Windows checkbox


        /**
         * Builder Pattern:
         * Creates complex objects step-by-step.
         * Avoids long constructors, makes object creation readable and flexible.
         */
        UserBuilder userBuilder = new UserBuilder.Builder()
                .name("Vinay")
                .age(24)
                .role("Dev")
                .build();


        /**
         * Prototype = clone objects instead of creating from scratch.
         *
         * Can implement shallow or deep copy depending on mutable fields.
         *
         * Reduces expensive initialization overhead.
         */
        Employee employee = new Employee("Vinay", 24);
        Employee clonedEmp = employee.clone();
        // Modify the clone
        clonedEmp.setName("V");

        System.out.println(employee); // Employee{name='Vinay', age=28}
        System.out.println(clonedEmp);   // Employee{name='Amit', age=28}




    }


}
