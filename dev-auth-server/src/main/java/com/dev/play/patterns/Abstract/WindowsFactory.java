package com.dev.play.patterns.Abstract;

/**
 * A pattern used to create a group (family) of related objects together, without specifying their exact classes.
 * You choose one factory, and it gives you all the related objects that match the same theme or family.
 */
public class WindowsFactory implements GUIFactory {
    @Override
    public Button createButton() {
        return new WinButton();
    }

    @Override
    public Checkbox createCheckbox() {
        return new WinCheckbox();
    }
}
