package com.beliautopart.beliautopart.helper;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by brandon on 04/06/16.
 */
public class Logic {
    public String thousand(String number){

        return NumberFormat.getIntegerInstance().format(Integer.parseInt(number.trim())).replace(",",".");
    }
}
