package no.nordicsemi.android.wifi.provisioner.feature.nfc.mapping

/**
 * Frequency utility class to provide frequency band information.
 * Inspired from [here](https://cs.android.com/android/platform/superproject/+/master:packages/modules/Wifi/framework/java/android/net/wifi/ScanResult.java)
 */
object Frequency {
    /** The unspecified value. */
    private const val UNSPECIFIED: Int = -1

    /** 2.4 GHz band first channel number. */
    private const val BAND_24_GHZ_FIRST_CH_NUM: Int = 1

    /** 2.4 GHz band frequency of first channel in MHz. */
    private const val BAND_24_GHZ_START_FREQ_MHZ: Int = 2412

    /** 2.4 GHz band frequency of last channel in MHz. */
    private const val BAND_24_GHZ_END_FREQ_MHZ: Int = 2484

    /** 5 GHz band first channel number. */
    private const val BAND_5_GHZ_FIRST_CH_NUM: Int = 32

    /** 5 GHz band frequency of first channel in MHz. */
    private const val BAND_5_GHZ_START_FREQ_MHZ: Int = 5160

    /** 5 GHz band frequency of last channel in MHz. */
    private const val BAND_5_GHZ_END_FREQ_MHZ: Int = 5885

    /** 6 GHz band first channel number. */
    private const val BAND_6_GHZ_FIRST_CH_NUM: Int = 1

    /** 6 GHz band frequency of first channel in MHz. */
    private const val BAND_6_GHZ_START_FREQ_MHZ: Int = 5955

    /** 6 GHz band frequency of last channel in MHz. */
    private const val BAND_6_GHZ_END_FREQ_MHZ: Int = 7115

    /**
     * The center frequency of the first 6Ghz preferred scanning channel, as defined by
     * IEEE802.11ax draft 7.0 section 26.17.2.3.3.
     */
    private const val BAND_6_GHZ_PSC_START_MHZ: Int = 5975

    /**
     * The number of MHz to increment in order to get the next 6Ghz preferred scanning channel
     * as defined by IEEE802.11ax draft 7.0 section 26.17.2.3.3.
     */
    private const val BAND_6_GHZ_PSC_STEP_SIZE_MHZ: Int = 80

    /** 6 GHz band operating class 136 channel 2 center frequency in MHz. */
    private const val BAND_6_GHZ_OP_CLASS_136_CH_2_FREQ_MHZ: Int = 5935

    /** 60 GHz band first channel number. */
    private const val BAND_60_GHZ_FIRST_CH_NUM: Int = 1

    /** 60 GHz band frequency of first channel in MHz. */
    private const val BAND_60_GHZ_START_FREQ_MHZ: Int = 58320

    /** 60 GHz band frequency of last channel in MHz. */
    private const val BAND_60_GHZ_END_FREQ_MHZ: Int = 70200

    /**
     * Utility function to check if a frequency within 2.4 GHz band.
     *
     * @param freqMhz frequency in MHz
     * @return true if within 2.4GHz, false otherwise
     */
    private fun is24GHz(freqMhz: Int): Boolean {
        return freqMhz in BAND_24_GHZ_START_FREQ_MHZ..BAND_24_GHZ_END_FREQ_MHZ
    }

    /**
     * Utility function to check if a frequency within 5 GHz band.
     *
     * @param freqMhz frequency in MHz
     * @return true if within 5GHz, false otherwise
     */
    private fun is5GHz(freqMhz: Int): Boolean {
        return freqMhz in BAND_5_GHZ_START_FREQ_MHZ..BAND_5_GHZ_END_FREQ_MHZ
    }

    /**
     * Utility function to check if a frequency within 6 GHz band.
     *
     * @param freqMhz
     * @return true if within 6GHz, false otherwise
     */
    private fun is6GHz(freqMhz: Int): Boolean {
        if (freqMhz == BAND_6_GHZ_OP_CLASS_136_CH_2_FREQ_MHZ) {
            return true
        }
        return (freqMhz in BAND_6_GHZ_START_FREQ_MHZ..BAND_6_GHZ_END_FREQ_MHZ)
    }

    /**
     * Utility function to check if a frequency is 6Ghz PSC channel.
     *
     * @param freqMhz
     * @return true if the frequency is 6GHz PSC, false otherwise
     */
    private fun is6GHzPsc(freqMhz: Int): Boolean {
        if (!is6GHz(freqMhz)) {
            return false
        }
        return (freqMhz - BAND_6_GHZ_PSC_START_MHZ) % BAND_6_GHZ_PSC_STEP_SIZE_MHZ == 0
    }

    /**
     * Utility function to check if a frequency within 60 GHz band
     * @param freqMhz
     * @return true if within 60GHz, false otherwise
     *
     * @hide
     */
    private fun is60GHz(freqMhz: Int): Boolean {
        return freqMhz in BAND_60_GHZ_START_FREQ_MHZ..BAND_60_GHZ_END_FREQ_MHZ
    }

    /**
     * Utility function to get the frequency band of a given frequency.
     *
     * @param frequency frequency in MHz
     * @return frequency band
     */
    fun get(frequency: Int): String {
        return when {
            is24GHz(frequency) -> "2.4 GHz"
            is5GHz(frequency) -> "5 GHz"
            is6GHz(frequency) -> "6 GHz"
            is6GHzPsc(frequency) -> "6 GHz PSC"
            is60GHz(frequency) -> "60 GHz"
            else -> "Unknown"
        }
    }

    /**
     * Utility function to convert frequency in MHz to channel number.
     *
     * @param freqMhz frequency in MHz
     * @return channel number associated with given frequency, [.UNSPECIFIED] if no match
     */
    fun toChannelNumber(freqMhz: Int): Int {
        when {
            freqMhz == 2484 -> {
                return 14
            }

            is24GHz(freqMhz) -> {
                return (freqMhz - BAND_24_GHZ_START_FREQ_MHZ) / 5 + BAND_24_GHZ_FIRST_CH_NUM
            }

            is5GHz(freqMhz) -> {
                return ((freqMhz - BAND_5_GHZ_START_FREQ_MHZ) / 5) + BAND_5_GHZ_FIRST_CH_NUM
            }

            is6GHz(freqMhz) -> {
                if (freqMhz == BAND_6_GHZ_OP_CLASS_136_CH_2_FREQ_MHZ) {
                    return 2
                }
                return ((freqMhz - BAND_6_GHZ_START_FREQ_MHZ) / 5) + BAND_6_GHZ_FIRST_CH_NUM
            }

            is60GHz(freqMhz) -> {
                return ((freqMhz - BAND_60_GHZ_START_FREQ_MHZ) / 2160) + BAND_60_GHZ_FIRST_CH_NUM
            }

            else -> return UNSPECIFIED
        }
    }

}
