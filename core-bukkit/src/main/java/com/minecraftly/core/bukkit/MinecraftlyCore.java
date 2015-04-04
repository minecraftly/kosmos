package com.minecraftly.core.bukkit;

import com.minecraftly.core.ContentOwner;
import com.minecraftly.core.bukkit.database.DatabaseManager;
import com.minecraftly.core.bukkit.language.LanguageManager;
import com.minecraftly.core.bukkit.module.ModuleManager;
import com.minecraftly.core.bukkit.user.UserManager;
import org.bukkit.plugin.Plugin;

import java.io.File;

/**
 * Created by Keir on 20/03/2015.
 */
public interface MinecraftlyCore extends Plugin, ContentOwner {

    DatabaseManager getDatabaseManager();

    LanguageManager getLanguageManager();

    UserManager getUserManager();

    ModuleManager getModuleManager();

    File getGeneralDataDirectory();

    File getBackupsDirectory();

}
