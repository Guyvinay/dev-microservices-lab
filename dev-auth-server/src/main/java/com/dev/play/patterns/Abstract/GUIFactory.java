package com.dev.play.patterns.Abstract;

/**
 * Abstract factory.
 *
 * A pattern used to create a group (family) of related objects together, without specifying their exact classes.
 * You choose one factory, and it gives you all the related objects that match the same theme or family.
 */
public interface GUIFactory {
    Button createButton();
    Checkbox createCheckbox();
}
