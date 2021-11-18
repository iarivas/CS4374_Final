package sereneseasons.api.config;

import com.google.common.collect.Maps;

import java.util.Map;

public abstract class SyncedConfig
{
    public Map<String, SyncedConfigEntry> optionsToSync = Maps.newHashMap();

    public static void addOption(final ISyncedOption option, final String defaultValue)
    {
        optionsToSync.put(option.getOptionName(), new SyncedConfigEntry(defaultValue));
    }

    public static boolean getBooleanValue(ISyncedOption option)
    {
        return Boolean.valueOf(getValue(option));
    }

    public static int getIntValue(ISyncedOption option)
    {
        return Integer.valueOf(getValue(option));
    }

    public static String getValue(ISyncedOption option)
    {
        String holdName = option.getOptionName();
        SyncedConfigEntry e = optionsToSync.get(holdName);
        return e.value;
    }

    public static void restoreDefaults()
    {
        for (SyncedConfigEntry entry : optionsToSync.values())
        {
            entry.value = entry.defaultValue;
        }
    }

    public static class SyncedConfigEntry
    {
        public String value;
        public final String defaultValue;

        public SyncedConfigEntry(final String defaultValue)
        {
            this.defaultValue = defaultValue;
            this.value = defaultValue;
        }
    }
}
