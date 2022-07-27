/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pro1041.utils;

import javax.swing.text.JTextComponent;

/**
 *
 * @author LaptopAZ.vn
 */
public class Validator {

    private static final String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
    private static final String PHONE_NUMBER_REGEX = "^(0?)(3[2-9]|5[6|8|9]|7[0|6-9]|8[0-6|8|9]|9[0-4|6-9])[0-9]{7}$";
    private static final String SPECIAL_REGEX = "[a-zA-Z0-9]*";
    

    //Validate Email
    public static boolean isValidEmail(JTextComponent txt, String message) {
        if (txt.getText().matches(EMAIL_REGEX)) {
            return false;
        }

        MsgBox.alert(null, message);
        txt.requestFocus();
        return true;
    }

    //Valudate số điện thoại
    public static boolean isValidPhone(JTextComponent txt, String message) {
        if (txt.getText().matches(PHONE_NUMBER_REGEX)) {
            return false;
        }

        MsgBox.alert(null, message);
        txt.requestFocus();
        return true;
    }
    
    //Validate Email
    public static boolean isContainSpecialCharacter(JTextComponent txt, String message) {
        if (txt.getText().matches(SPECIAL_REGEX)) {
            return false;
        }

        MsgBox.alert(null, message);
        txt.requestFocus();
        return true;
    }

    // valudate check null
    public static boolean isNull(JTextComponent txt, String message) {
        if (txt.getText().trim().isEmpty()) {
            MsgBox.alert(null, message);
            txt.requestFocus();
            return true;
        }
        return false;
    }
}
