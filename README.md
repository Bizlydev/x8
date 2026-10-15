# X8

X8 is a private-use, Persian-first Android ledger app for tracking customer credit and payments.

## Current status
This repository contains the initial Android application scaffold only. It is **not yet a finished financial app** and no APK has been built or verified.

## Planned architecture
- Kotlin + Jetpack Compose for Android UI
- Room for durable local-first storage
- Supabase for authenticated remote access and synchronization
- Integer toman amounts (never floating-point money)
- Reversal entries instead of deleting posted financial transactions
- Role-based access, audit history, validated backup/restore, and tests

## Security rules
- Never commit passwords, access tokens, service-role keys, signing keys, customer data, or real transaction data.
- The Supabase publishable/anon key may be used in a client only with correctly configured Row Level Security (RLS); it is not a substitute for RLS.
- Never include a Supabase service-role key in the Android app.
- Validate all authorization and financial rules on the trusted database/server side.
- Treat public source code as visible to everyone.

## Build
Open this repository in Android Studio with a compatible Android SDK and Gradle installation. The project currently has no Gradle wrapper files; those must be added before wrapper-based builds or CI can run.

## Development roadmap
1. Establish a reproducible Android build.
2. Implement and test local Room entities and repositories.
3. Add authentication and authorization.
4. Implement customer accounts and ledger posting/reversal.
5. Add sync, reports, reminders, backup/restore, and release packaging.
