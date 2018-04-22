/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Composers;

/**
 *
 * @author darylcecile
 */
public final class Log {
    
    public static void Out(Object... parts){
        System.out.println( FormParts(parts) );
    }
    
    public static void Err(Object... parts){
        System.err.println( FormParts(parts) );
    }
    
    private static String FormParts(Object... parts){
        String res = "";
        for (Object part : parts) {
            res += String.valueOf(part) + ' ';
        }
        return res.substring(0,res.length() - 1);
    }
    
}
