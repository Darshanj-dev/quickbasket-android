# QuickBasket

QuickBasket is a native Android grocery shopping prototype built with Java. It demonstrates the customer shopping flow for a quick commerce app, with a small bundled catalog and offline persistence.

## Features

- Browse a sample grocery catalog by category and search by product name.
- Open product details and add items to a quantity-aware cart.
- Review a bill summary and prevent checkout when the cart is empty or no delivery address is selected.
- Add, edit, select, and delete saved delivery addresses.
- Place a local demo order and view its item list and sample packing status in My Orders.
- Keep catalog data available offline and save cart, addresses, and orders on the device.
- Show on-device notifications for demo order updates and promotional offers, with no Firebase setup required.

## Build and run

1. Open this `android-app` folder in Android Studio.
2. Let Android Studio sync the Gradle project.
3. Select an Android emulator or device and run the `app` configuration.

The debug APK is generated at `app/build/outputs/apk/debug/app-debug.apk` after a successful build. The project uses Android Gradle Plugin 8.13.2, Gradle 8.13, Java 17 source compatibility, and Android SDK 35. No Firebase project or service credentials are required.

## Scope

This is an offline-first learning/demo app. Product data and orders are local sample data. Android notifications are generated on-device for order confirmations and the sample promotion action. Server-side real-time order tracking, remote push delivery, payment processing, maps, and authentication are not connected. Order statuses shown in the app are sample local statuses.
