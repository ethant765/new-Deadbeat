/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.core;

/**
 *
 * @author darylcecile
 * 
 */

@FunctionalInterface
public interface ITask<T> {
    void Run(T... results);
}