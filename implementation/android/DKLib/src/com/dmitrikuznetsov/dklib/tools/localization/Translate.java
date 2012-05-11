package com.dmitrikuznetsov.dklib.tools.localization;

import java.util.Locale;

import android.content.Context;
import android.content.res.Configuration;


/**
 * Class to be used for translating text to different languages
 * @author dmitrikuznetsov
 *
 */
public class Translate 
{
	/**
	 * When resource ID is specified, it will be loaded from Android
	 * automatically
	 */
	int resourceID = -1;
	
	
	/**
	 * When message override is specified it will be used instead of 
	 * resource ID to override translation with this specific text only
	 */
	String text = null;
	
	
	/**
	 * Constructor used for overriding the initial translation text
	 * @param text Text to be used for override
	 */
	public Translate(String text)
	{
		this.text = text;
	}
	
	
	/**
	 * Constructor used for initial value of translated text to be used
	 * (Loaded and assigned by android automatically)
	 * @param resourceID
	 */
	public Translate(int resourceID)
	{
		this.resourceID = resourceID;
	}
	
	/**
	 * Translates text from resourceID,
	 * 
	 * If text was specified directly, then it will override any resourceID values specified.
	 * 
	 * @param context	Context required for retrieving and automatically loading correct locale translations
	 * @return
	 */
	public String translate(Context context)
	{
		if( text == null )
		{
			return context.getString(resourceID);
		}
		else
		{
			return text;
		}
	}
	
	/**
	 * Creates an instance of {@link Translate} class from the specified text
	 * 
	 * @param text	Text to use for translation
	 * 
	 * @return		Instance of {@link Translate} class
	 */
	public static Translate translate(String text)
	{
		return new Translate(text);
	}
	
	/**
	 * Retrieves currently used locale
	 * 
	 * @param context	Context to retrieve locale from
	 * 
	 * @return			Currently used locale
	 */
	public static Locale getCurrentLocale(Context context)
	{
		return context.getResources().getConfiguration().locale;
	}
	
	
	/**
	 * Forces to use specified locale
	 * 
	 * <b>Beware ! Avoid using this method if possible!</b>
	 * 
	 * @param context 	Context who's locale needs to be changed
	 * @param locale	Locale to set
	 */
	public static void setCurrentLocale(Context context, Locale locale)
	{
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		context.getResources().updateConfiguration(config,
			      context.getResources().getDisplayMetrics());
	}
	
	
	/**
	 * Changes translation, to be used and override any default translation
	 * 
	 * @param text	Text translation to use now
	 */
	public void setTranslation(String text)
	{
		this.text = text;
	}
}
