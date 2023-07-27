/*
 * Copyright 2014-2017 Eduard Ereza Martínez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.com.shadowless.baseview.crash.config;

import android.app.Activity;

import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Modifier;

import cn.com.shadowless.baseview.crash.CustomActivityOnCrash;


/**
 * The type Cao config.
 *
 * @author sHadowLess
 */
public class CrashConfig implements Serializable {

    /**
     * The interface Background mode.
     */
    @IntDef({BACKGROUND_MODE_CRASH, BACKGROUND_MODE_SHOW_CUSTOM, BACKGROUND_MODE_SILENT})
    @Retention(RetentionPolicy.SOURCE)
    private @interface BackgroundMode {
        //I hate empty blocks
    }

    /**
     * The constant BACKGROUND_MODE_SILENT.
     */
    public static final int BACKGROUND_MODE_SILENT = 0;
    /**
     * The constant BACKGROUND_MODE_SHOW_CUSTOM.
     */
    public static final int BACKGROUND_MODE_SHOW_CUSTOM = 1;
    /**
     * The constant BACKGROUND_MODE_CRASH.
     */
    public static final int BACKGROUND_MODE_CRASH = 2;

    /**
     * The Background mode.
     */
    private int backgroundMode = BACKGROUND_MODE_SHOW_CUSTOM;
    /**
     * The Enabled.
     */
    private boolean enabled = true;
    /**
     * The Show error details.
     */
    private boolean showErrorDetails = true;
    /**
     * The Show restart button.
     */
    private boolean showRestartButton = true;
    /**
     * The Log error on restart.
     */
    private boolean logErrorOnRestart = true;
    /**
     * The Track activities.
     */
    private boolean trackActivities = false;
    /**
     * The Min time between crashes ms.
     */
    private int minTimeBetweenCrashesMs = 3000;
    /**
     * The Error drawable.
     */
    private Integer errorDrawable = null;
    /**
     * The Error activity class.
     */
    private Class<? extends Activity> errorActivityClass = null;
    /**
     * The Restart activity class.
     */
    private Class<? extends Activity> restartActivityClass = null;
    /**
     * The Custom crash data collector.
     */
    private CustomActivityOnCrash.CustomCrashDataCollector customCrashDataCollector = null;
    /**
     * The Event listener.
     */
    private CustomActivityOnCrash.EventListener eventListener = null;

    /**
     * Gets background mode.
     *
     * @return the background mode
     */
    @BackgroundMode
    public int getBackgroundMode() {
        return backgroundMode;
    }

    /**
     * Sets background mode.
     *
     * @param backgroundMode the background mode
     */
    public void setBackgroundMode(@BackgroundMode int backgroundMode) {
        this.backgroundMode = backgroundMode;
    }

    /**
     * Is enabled boolean.
     *
     * @return the boolean
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets enabled.
     *
     * @param enabled the enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Is show error details boolean.
     *
     * @return the boolean
     */
    public boolean isShowErrorDetails() {
        return showErrorDetails;
    }

    /**
     * Sets show error details.
     *
     * @param showErrorDetails the show error details
     */
    public void setShowErrorDetails(boolean showErrorDetails) {
        this.showErrorDetails = showErrorDetails;
    }

    /**
     * Is show restart button boolean.
     *
     * @return the boolean
     */
    public boolean isShowRestartButton() {
        return showRestartButton;
    }

    /**
     * Sets show restart button.
     *
     * @param showRestartButton the show restart button
     */
    public void setShowRestartButton(boolean showRestartButton) {
        this.showRestartButton = showRestartButton;
    }

    /**
     * Is log error on restart boolean.
     *
     * @return the boolean
     */
    public boolean isLogErrorOnRestart() {
        return logErrorOnRestart;
    }

    /**
     * Sets log error on restart.
     *
     * @param logErrorOnRestart the log error on restart
     */
    public void setLogErrorOnRestart(boolean logErrorOnRestart) {
        this.logErrorOnRestart = logErrorOnRestart;
    }

    /**
     * Is track activities boolean.
     *
     * @return the boolean
     */
    public boolean isTrackActivities() {
        return trackActivities;
    }

    /**
     * Sets track activities.
     *
     * @param trackActivities the track activities
     */
    public void setTrackActivities(boolean trackActivities) {
        this.trackActivities = trackActivities;
    }

    /**
     * Gets min time between crashes ms.
     *
     * @return the min time between crashes ms
     */
    public int getMinTimeBetweenCrashesMs() {
        return minTimeBetweenCrashesMs;
    }

    /**
     * Sets min time between crashes ms.
     *
     * @param minTimeBetweenCrashesMs the min time between crashes ms
     */
    public void setMinTimeBetweenCrashesMs(int minTimeBetweenCrashesMs) {
        this.minTimeBetweenCrashesMs = minTimeBetweenCrashesMs;
    }

    /**
     * Gets error drawable.
     *
     * @return the error drawable
     */
    @Nullable
    @DrawableRes
    public Integer getErrorDrawable() {
        return errorDrawable;
    }

    /**
     * Sets error drawable.
     *
     * @param errorDrawable the error drawable
     */
    public void setErrorDrawable(@Nullable @DrawableRes Integer errorDrawable) {
        this.errorDrawable = errorDrawable;
    }

    /**
     * Gets error activity class.
     *
     * @return the error activity class
     */
    @Nullable
    public Class<? extends Activity> getErrorActivityClass() {
        return errorActivityClass;
    }

    /**
     * Sets error activity class.
     *
     * @param errorActivityClass the error activity class
     */
    public void setErrorActivityClass(@Nullable Class<? extends Activity> errorActivityClass) {
        this.errorActivityClass = errorActivityClass;
    }

    /**
     * Gets custom crash data collector.
     *
     * @return the custom crash data collector
     */
    @Nullable
    public CustomActivityOnCrash.CustomCrashDataCollector getCustomCrashDataCollector() {
        return customCrashDataCollector;
    }

    /**
     * Sets custom crash data collector.
     *
     * @param collector the collector
     */
    public void setCustomCrashDataCollector(@Nullable CustomActivityOnCrash.CustomCrashDataCollector collector) {
        this.customCrashDataCollector = collector;
    }

    /**
     * Gets restart activity class.
     *
     * @return the restart activity class
     */
    @Nullable
    public Class<? extends Activity> getRestartActivityClass() {
        return restartActivityClass;
    }

    /**
     * Sets restart activity class.
     *
     * @param restartActivityClass the restart activity class
     */
    public void setRestartActivityClass(@Nullable Class<? extends Activity> restartActivityClass) {
        this.restartActivityClass = restartActivityClass;
    }

    /**
     * Gets event listener.
     *
     * @return the event listener
     */
    @Nullable
    public CustomActivityOnCrash.EventListener getEventListener() {
        return eventListener;
    }

    /**
     * Sets event listener.
     *
     * @param eventListener the event listener
     */
    public void setEventListener(@Nullable CustomActivityOnCrash.EventListener eventListener) {
        this.eventListener = eventListener;
    }

    /**
     * The type Builder.
     */
    public static class Builder {
        /**
         * The Config.
         */
        private CrashConfig config;

        /**
         * Create builder.
         *
         * @return the builder
         */
        @NonNull
        public static Builder create() {
            Builder builder = new Builder();
            CrashConfig currentConfig = CustomActivityOnCrash.getConfig();

            CrashConfig config = new CrashConfig();
            config.backgroundMode = currentConfig.backgroundMode;
            config.enabled = currentConfig.enabled;
            config.showErrorDetails = currentConfig.showErrorDetails;
            config.showRestartButton = currentConfig.showRestartButton;
            config.logErrorOnRestart = currentConfig.logErrorOnRestart;
            config.trackActivities = currentConfig.trackActivities;
            config.minTimeBetweenCrashesMs = currentConfig.minTimeBetweenCrashesMs;
            config.errorDrawable = currentConfig.errorDrawable;
            config.errorActivityClass = currentConfig.errorActivityClass;
            config.customCrashDataCollector = currentConfig.customCrashDataCollector;
            config.restartActivityClass = currentConfig.restartActivityClass;
            config.eventListener = currentConfig.eventListener;

            builder.config = config;

            return builder;
        }

        /**
         * Defines if the error activity must be launched when the app is on background.
         * BackgroundMode.BACKGROUND_MODE_SHOW_CUSTOM: launch the error activity when the app is in background,
         * BackgroundMode.BACKGROUND_MODE_CRASH: launch the default system error when the app is in background,
         * BackgroundMode.BACKGROUND_MODE_SILENT: crash silently when the app is in background,
         * The default is BackgroundMode.BACKGROUND_MODE_SHOW_CUSTOM (the app will be brought to front when a crash occurs).
         *
         * @param backgroundMode the background mode
         * @return the builder
         */
        @NonNull
        public Builder backgroundMode(@BackgroundMode int backgroundMode) {
            config.backgroundMode = backgroundMode;
            return this;
        }

        /**
         * Defines if CustomActivityOnCrash crash interception mechanism is enabled.
         * Set it to true if you want CustomActivityOnCrash to intercept crashes,
         * false if you want them to be treated as if the library was not installed.
         * The default is true.
         *
         * @param enabled the enabled
         * @return the builder
         */
        @NonNull
        public Builder enabled(boolean enabled) {
            config.enabled = enabled;
            return this;
        }

        /**
         * Defines if the error activity must shown the error details button.
         * Set it to true if you want to show the full stack trace and device info,
         * false if you want it to be hidden.
         * The default is true.
         *
         * @param showErrorDetails the show error details
         * @return the builder
         */
        @NonNull
        public Builder showErrorDetails(boolean showErrorDetails) {
            config.showErrorDetails = showErrorDetails;
            return this;
        }

        /**
         * Defines if the error activity should show a restart button.
         * Set it to true if you want to show a restart button,
         * false if you want to show a close button.
         * Note that even if restart is enabled but you app does not have any launcher activities,
         * a close button will still be used by the default error activity.
         * The default is true.
         *
         * @param showRestartButton the show restart button
         * @return the builder
         */
        @NonNull
        public Builder showRestartButton(boolean showRestartButton) {
            config.showRestartButton = showRestartButton;
            return this;
        }

        /**
         * Defines if the stack trace must be logged again once the custom activity is shown.
         * Set it to true if you want to log the stack trace again,
         * false if you don't want the extra logging.
         * This option exists because the default Android Studio logcat view only shows the output
         * of the current process, and since the error activity runs on a new process,
         * you can't see the previous output easily.
         * Internally, it's logged when getConfigFromIntent() is called.
         * The default is true.
         *
         * @param logErrorOnRestart the log error on restart
         * @return the error on restart
         */
        @NonNull
        public Builder logErrorOnRestart(boolean logErrorOnRestart) {
            config.logErrorOnRestart = logErrorOnRestart;
            return this;
        }

        /**
         * Defines if the activities visited by the user should be tracked
         * so they are reported when an error occurs.
         * The default is false.
         *
         * @param trackActivities the track activities
         * @return the builder
         */
        @NonNull
        public Builder trackActivities(boolean trackActivities) {
            config.trackActivities = trackActivities;
            return this;
        }

        /**
         * Defines the time that must pass between app crashes to determine that we are not
         * in a crash loop. If a crash has occurred less that this time ago,
         * the error activity will not be launched and the system crash screen will be invoked.
         * The default is 3000.
         *
         * @param minTimeBetweenCrashesMs the min time between crashes ms
         * @return the builder
         */
        @NonNull
        public Builder minTimeBetweenCrashesMs(int minTimeBetweenCrashesMs) {
            config.minTimeBetweenCrashesMs = minTimeBetweenCrashesMs;
            return this;
        }

        /**
         * Defines which drawable to use in the default error activity image.
         * Set this if you want to use an image other than the default one.
         * The default is R.drawable.customactivityoncrash_error_image (a cute upside-down bug).
         *
         * @param errorDrawable the error drawable
         * @return the builder
         */
        @NonNull
        public Builder errorDrawable(@Nullable @DrawableRes Integer errorDrawable) {
            config.errorDrawable = errorDrawable;
            return this;
        }

        /**
         * Sets the error activity class to launch when a crash occurs.
         * If null, the default error activity will be used.
         *
         * @param errorActivityClass the error activity class
         * @return the builder
         */
        @NonNull
        public Builder errorActivity(@Nullable Class<? extends Activity> errorActivityClass) {
            config.errorActivityClass = errorActivityClass;
            return this;
        }

        /**
         * Sets the main activity class that the error activity must launch when a crash occurs.
         * If not set or set to null, the default launch activity will be used.
         * If your app has no launch activities and this is not set, the default error activity will close instead.
         *
         * @param restartActivityClass the restart activity class
         * @return the builder
         */
        @NonNull
        public Builder restartActivity(@Nullable Class<? extends Activity> restartActivityClass) {
            config.restartActivityClass = restartActivityClass;
            return this;
        }

        /**
         * Sets an event listener to be called when events occur, so they can be reported
         * by the app as, for example, Google Analytics events.
         * If not set or set to null, no events will be reported.
         *
         * @param eventListener The event listener.
         * @return the builder
         * @throws IllegalArgumentException if the eventListener is an inner or anonymous class
         */
        @NonNull
        public Builder eventListener(@Nullable CustomActivityOnCrash.EventListener eventListener) {
            if (eventListener != null && eventListener.getClass().getEnclosingClass() != null && !Modifier.isStatic(eventListener.getClass().getModifiers())) {
                throw new IllegalArgumentException("The event listener cannot be an inner or anonymous class, because it will need to be serialized. Change it to a class of its own, or make it a static inner class.");
            } else {
                config.eventListener = eventListener;
            }
            return this;
        }

        /**
         * Sets the custom data collector class to invoke when a crash occurs.
         * If not set or set to null, no custom data will be collected.
         *
         * @param collector The custom data collector.
         * @return the builder
         * @throws IllegalArgumentException if the collector is an inner or anonymous class
         */
        @NonNull
        public Builder customCrashDataCollector(@Nullable CustomActivityOnCrash.CustomCrashDataCollector collector) {
            if (collector != null && collector.getClass().getEnclosingClass() != null && !Modifier.isStatic(collector.getClass().getModifiers())) {
                throw new IllegalArgumentException("The custom data collector cannot be an inner or anonymous class, because it will need to be serialized. Change it to a class of its own, or make it a static inner class.");
            } else {
                config.customCrashDataCollector = collector;
            }
            return this;
        }

        /**
         * Get cao config.
         *
         * @return the cao config
         */
        @NonNull
        public CrashConfig get() {
            return config;
        }

        /**
         * Apply.
         */
        public void apply() {
            CustomActivityOnCrash.setConfig(config);
        }
    }


}
