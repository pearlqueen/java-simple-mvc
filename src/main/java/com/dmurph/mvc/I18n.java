/**
 * Created at Jun 8, 2010, 1:22:04 AM
 */
package com.dmurph.mvc;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.KeyStroke;

/**
 * @author Daniel Murphy
 *
 */
public class I18n {
	
	/**
	 * Get the text for this key. The text has no special control characters in
	 * it, and can be presented to the user.
	 * <p>
	 * For example, if the localization file has the line
	 * <code>copy = &amp;Copy [accel C]</code>, the string "Copy" is returned.
	 * </p>
	 * 
	 * @param key
	 *            the key to look up in the localization file
	 * @return the text
	 */
	public static String getText(String key) {
		String value = null;
		
		try {
			value = msg.getString(key);
		} catch (MissingResourceException e) {
			System.err.println("Unable to find the translation for the key: " + key);
			return key;
		};
		
		StringBuffer buf = new StringBuffer();
		
		int n = value.length();
		boolean ignore = false;
		for (int i = 0; i < n; i++) {
			char c = value.charAt(i);
			switch (c) {
				case '&' :
					continue;
				case '[' :
					ignore = true;
					break;
				case ']' :
					ignore = false;
					break;
				default :
					if (!ignore) {
						buf.append(c);
					}
			}
		}
		
		return buf.toString().trim();
	}
	
	/**
	 * Look up a translation key with each {n} replaced
	 * with a value in the array
	 * 
	 * @param argKey
	 * @param argReplacing
	 * @return
	 */
	public static String getText(String argKey, String... argReplacing) {
		String text = getText(argKey);
		
		for (int i = 0; i < argReplacing.length; i++) {
			text = text.replace("{" + i + "}", argReplacing[i]);
		}
		
		return text;
	}
	
	/**
	 * Gets and integer value of the key.  If the 
	 * value isn't able to parse as an integer,
	 * null is returned
	 * @param argKey
	 * @return
	 */
	public static Integer getInteger(String argKey){
		String text = getText(argKey);
		try{
			int i = Integer.parseInt(text);
			return i;
		}catch( NumberFormatException e){
			return null;
		}
	}
	
	/**
	 * Gets the boolean value of the key.  If the 
	 * value is anything but "true", then false
	 * is returned
	 * @param argKey
	 * @return
	 */
	public static Boolean getBoolean(String argKey){
		String text = getText(argKey);
		try{
			boolean tf = Boolean.parseBoolean(text);
			return tf;
		}catch( NumberFormatException e){
			return null;
		}
	}
	
	
	
	/**
	 * Get the keystroke string for this key. This string can be passed directly
	 * to the Keystroke.getKeyStroke() method.
	 * <p>
	 * For example, if the localization file has the line
	 * <code>copy = &amp;Copy [accel C]</code>, the string "control C" is returned (or on
	 * the Mac, "meta C").
	 * </p>
	 * <p>
	 * If the string has no [keystroke] listed, null is returned.
	 * </p>
	 * 
	 * @param key
	 *            the key to look up in the localization file
	 * @return the keystroke
	 */
	public static KeyStroke getKeyStroke(String key) {
		String value = msg.getString(key);
		
		int left = value.indexOf('[');
		int right = value.indexOf(']');
		
		if (left == -1 || right == -1) {
			return null;
		}
		
		String stroke = value.substring(left + 1, right).trim();
		
		// accel = command (in java-ese: "meta") on mac, control on pc
		// String accel = (App.platform.isMac() ? "meta" : "control");
		// stroke = StringUtils.substitute(stroke, "accel", accel);
		
		return KeyStroke.getKeyStroke(stroke);
	}
	
	/**
	 * Get the mnemonic character this key.
	 * <p>
	 * For example, if the localization file has the line
	 * <code>copy = &amp;Copy [accel C]</code>, the character "C" is returned.
	 * </p>
	 * <p>
	 * If the string has no &amp;mnemonic listed, null is returned.
	 * </p>
	 * 
	 * @param key
	 *            the key to look up in the localization file
	 * @return the integer representing the mnemonic character
	 */
	public static Integer getMnemonic(String key) {
		String value = msg.getString(key);
		
		int amp = value.indexOf('&');
		
		if (amp == -1 || amp == value.length() - 1) {
			return null;
		}
		
		return new Integer(Character.toUpperCase(value.charAt(amp + 1)));
	}
	
	/**
	 * Get the position of the mnemonic character in the string
	 * Used for setDisplayedMnemonicIndex
	 * 
	 * @param key
	 * @return an Integer, or null
	 */
	public static Integer getMnemonicPosition(String key) {
		String value = msg.getString(key);
		
		int amp = value.indexOf('&');
		
		if (amp == -1 || amp == value.length() - 1) {
			return null;
		}
		
		return amp;
	}
	
	// the resource bundle to use
	private final static ResourceBundle msg;
	
	static {
		ResourceBundle bundle;
		try {
			bundle = ResourceBundle.getBundle("locale/java-simple-mvc");
		} catch (MissingResourceException mre) {
			try {
				bundle = ResourceBundle.getBundle("java-simple-mvc");
			} catch (MissingResourceException mre2) {
				mre2.printStackTrace();
				bundle = new DefaultResourceBundle();
			}
		}
		msg = bundle;
	}
}
