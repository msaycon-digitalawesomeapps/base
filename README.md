# Exclusive Setup Guide

## Introduction

This project serves as a base project solution for Dispensary Apps. This README will guide you through the process of setting up this project.

## Requirements

    Android Studio (Version Giraffe | 2022.3.1 or higher)
    JDK 17
    Gradle 8.0

## Quick Start

### Clone these repositories

#### Base Project:
```bash
  git clone https://github.com/Digital-Awesome/dm_android_exclusive
```

#### Domain Layer:
```bash
  git clone https://github.com/Digital-Awesome/dispensary-domain-android
```

#### UI Components:
```bash
  git clone https://github.com/Digital-Awesome/dispensary-components-android
```



### Brand Customization

#### Theme
To set your brand theme attributes, modify the values in res/values/themes.xml.

```xml
    <resources>
    <!-- Base application theme. -->
    <style name="Theme.Starting" parent="Theme.SplashScreen">
        <item name="android:windowBackground">@color/white</item>
        <item name="windowSplashScreenBackground">@color/white</item>
        <item name="postSplashScreenTheme">@style/Theme.Exclusive</item>

        <!-- Use windowSplashScreenAnimatedIcon to add either a drawable or an
             animated drawable. One of these is required. -->
        <item name="windowSplashScreenAnimatedIcon">@mipmap/ic_launcher_foreground</item>

    </style>

    <style name="Theme.NoBackgroundDialogTheme" parent="da_components_Theme">
        <item name="android:windowBackground">@android:color/transparent</item>
        <item name="android:backgroundDimEnabled">true</item>
        <item name="android:windowIsFloating">false</item>
        <item name="android:windowSoftInputMode">adjustResize</item>
    </style>

    <style name="Theme.Exclusive" parent="da_components_Theme">
    <!-- Primary brand color. -->
    <item name="colorPrimary">@color/black</item>
    <item name="android:colorPrimary">@color/black</item>
    <item name="android:colorPrimaryDark">@color/black</item>
    <item name="colorPrimaryVariant">@color/da_components_color_grey_900</item>
    <item name="colorOnPrimary">@color/white</item>
    <!-- Secondary brand color. -->
    <item name="colorSecondary">@color/white</item>
    <item name="colorSecondaryVariant">@color/da_components_color_grey_900</item>
    <item name="colorOnSecondary">@color/black</item>
    <item name="android:windowSoftInputMode">adjustResize</item>

    <item name="da_components_themeColorPrimaryButtonGradientBackgroundStart">@color/black</item>
    <item name="da_components_themeColorPrimaryButtonGradientBackgroundEnd">@color/black</item>
    <item name="da_components_themeColorPrimaryButtonGradientBackgroundPressedStart">@color/da_components_color_grey_900</item>
    <item name="da_components_themeColorPrimaryButtonGradientBackgroundPressedEnd">@color/da_components_color_grey_900</item>
    <item name="da_components_themeColorPrimaryButtonBackgroundDisabled">@color/da_components_color_grey_500</item>
    <item name="da_components_themeColorPrimaryButtonProgress">@color/black</item>
    <item name="da_components_themeColorPrimaryButtonText">@color/da_components_color_white</item>
    <item name="da_components_themeColorPrimaryButtonTextDisabled">@color/da_components_color_grey_200</item>
    <!-- more attributes, see da_components_Theme from components -->
</resources>
```

### Logos and Assets

Replace the existing logos and assets in the res/drawable directory.

### Strings and Labels
Edit the res/values/strings.xml to reflect brand-specific text.

```xml
<resources>
    <string name="app_name">Dispensary Name</string>
    <!-- more strings -->
</resources>
```


### Troubleshooting

If you encounter any issues, check the following:

        Ensure all dependencies are properly installed.
        Ensure you have the correct Android SDK and JDK versions.
        Clear the project cache and rebuild.
