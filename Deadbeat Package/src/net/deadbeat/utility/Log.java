/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.utility;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author darylcecile
 */
public final class Log {
    
    public static void Out(Object... parts){
        System.out.println( FormParts(parts) );
    }
    
    public static void Reminder(Object... parts){
        Err("\n", BuildCenterTitle(30, "") );
        Err("\tREMEMBER: ", FormParts(parts),"\n");
        Err( BuildGroupFooter(30, "") );
    }
    
    public static void Err(Object... parts){
        System.err.println( FormParts(parts) );
    }
    
    public static void Throw(Exception e){
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        
        List<StackTraceElement> stack = Arrays.asList(stackTrace).subList(2, stackTrace.length);
        Collections.reverse(stack);
        
        StackTraceElement lastStackItem = stack.get(stack.size()-1);
        int lineNumber = lastStackItem.getLineNumber();
        
        Err( "\n", BuildGroupHeader(50," Error Thrown ") , "\n");
        Err( BuildCenterTitle(50,"Error on line " + lineNumber + " in " + lastStackItem.getFileName()) , "\n");
        
        Err("Message:");
        Err(e.getLocalizedMessage(),"\n");
        
        Err("Journey:");
        for (StackTraceElement stackItem : stack){
            Err("\t", "Called", "'" + stackItem.getMethodName() + "()'" , "in", stackItem.getClassName() + "("+stackItem.getFileName()+")" );
        }
        
        Err("\nCauses chain:");
        Throwable te = e.getCause();
        int depth = 4;
        while (te != null && depth > 0){
            Err("\t",te.getStackTrace()[0].getLineNumber(),te.getLocalizedMessage());
            te = te.getCause();
            depth--;
        }
        
        Err("\n", BuildGroupFooter(50,"Error Thrown") ,"\n" );
    }
    
    private static String BuildGroupHeader(int len,String title){
        int lsl = len - title.length();
        String filler = String.join("", Collections.nCopies(lsl/2, "*"));
        String r = filler + title + filler;
        return r;
    }
    
    private static String BuildCenterTitle(int len,String heading){
        int lsl = (len/2) - (heading.length()/2);
        String filler = String.format("%-" + lsl + "s","");
        String r = filler + String.format("%" + lsl + "s", heading) + filler;
        return r;
    }
    
    private static String BuildGroupFooter(int len,String title){
        int lsl = len + title.length();
        return String.join("", Collections.nCopies(lsl, "*"));
    }
    
    private static String FormParts(Object... parts){
        String res = "";
        for (Object part : parts) {
            res += String.valueOf(part) + ' ';
        }
        return res.substring(0,res.length() - 1);
    }
    
}
