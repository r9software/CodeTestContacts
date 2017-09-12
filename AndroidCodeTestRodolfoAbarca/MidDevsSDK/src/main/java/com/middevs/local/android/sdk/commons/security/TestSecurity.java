/******************************************************************************
 * Copyright © 2015-7532 NOX, Inc. [NEPOLIX]-(Behrooz Shahriari)              *
 *           All rights reserved.                                             *
 *                                                                            *
 *     The source code, other & all material, and documentation               *
 *     contained herein are, and remains the property of HEX Inc.             *
 *     and its suppliers, if any. The intellectual and technical              *
 *     concepts contained herein are proprietary to HEX Inc. and its          *
 *     suppliers and may be covered by U.S. and Foreign Patents, patents      *
 *     in process, and are protected by trade secret or copyright law.        *
 *     Dissemination of the foregoing material or reproduction of this        *
 *     material is strictly forbidden forever.                                *
 ******************************************************************************/

package com.middevs.local.android.sdk.commons.security;


import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * @author MidDevs
 * @since 11/22/16
 */
class TestSecurity {

    public static void main(String[] args)
            throws
            IOException,
            GeneralSecurityException {
        /**
         *  openssl genpkey -algorithm RSA -out private.pem -pkeyopt rsa_keygen_bits:8192
         *  openssl rsa -pubout -in private.pem -out public.pem
         */

//			String key    = AES.createRandomAESKey ( );
//			String encKey = RSA.encrypt ( key, RSA.getDefaultPublicKey ( ) );
//			String key_   = RSA.decrypt ( encKey, RSA.getDefaultPrivateKey ( ) );
//			System.out.println ( "KEY=" + key );
//			System.out.println ( "ENC_KEY=" + encKey );
//			System.out.println ( "key_=" + key_ );
//
//			String text = Utils.randomBase64String ( 40 );
//			System.out.println ( "TEXT=" + text );
//			String cipher = AES.encrypt ( text, key_ );
//			System.out.println ( "CIPHER=" + cipher );
//			System.out.println ( "TEXT=" + AES.decrypt ( cipher, key_ ) );

        //convert the RSA keys to file format
//			ArrayList< String > list = new ArrayList<> ( );
//			String              s1   = RSA.PRIVATE_KEY;
//			while ( !s1.isEmpty ( ) )
//			{
//				 int len = 64;
//				 if ( len > s1.length ( ) ) len = s1.length ( );
//				 String t = s1.substring ( 0, len );
//				 s1 = s1.substring ( len );
//				 list.add ( t );
//			}
//			for ( String ts : list ) System.out.println ( "-----BEGIN PUBLIC KEY-----\n"+ ts +"-----END PUBLIC KEY-----");

    }


}
