/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import server.BCrypt;

/**
 *
 * @author Faludi Péter
 */
public class BcryptHashing { 

    
   
    public static String makeSaltedHash(String originalPassword){
        System.out.println(originalPassword + "  " + BCrypt.hashpw(originalPassword, BCrypt.gensalt(12)));
        return BCrypt.hashpw(originalPassword, BCrypt.gensalt(12));
    }
    
    public static boolean isMatched(String originalPassword,String generatedSecuredPasswordHash){
        return BCrypt.checkpw(originalPassword, generatedSecuredPasswordHash);
    }
    
}
