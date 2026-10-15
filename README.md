# X8

X8 is a Persian-first Android ledger app for tracking customer credit and payments.

## Implemented in the current scaffold
- Kotlin + Jetpack Compose application entry point
- Room database for durable local customer and ledger storage
- Customer create/list/detail UI
- Credit and payment entry UI
- Integer toman amounts (no floating-point money)
- Local transaction posting in a Room transaction
- Payment validation to reject payments above the current positive balance
- UUID idempotency token generated per local ledger entry (sync enforcement is not implemented yet)

## Important limitations
- This is an early development build, not a production-ready financial app.
- No APK build has yet been confirmed.
- Local data is not encrypted by this implementation.
- Authentication, manager/employee roles, cloud sync, remote authorization, reversal/audit workflows, reports, backup/restore, and automated tests remain unfinished.
- The payment validation currently assumes a positive customer balance represents an amount owed to the shop. Credit balances and reversal workflows need fuller domain modeling.
- The current UI is a first functional slice; it has not yet been device-tested.

## Architecture
- Kotlin + Jetpack Compose
- Room / SQLite for local-first persistence
- Supabase planned for authenticated remote access and synchronization

## Security rules
- Never commit passwords, access tokens, service-role keys, signing keys, customer data, or real transaction data.
- Never include a Supabase service-role key in the Android app.
- Client-side checks are not security boundaries; enforce authorization and financial rules on the trusted database/server side.
- Treat public source code as visible to everyone.

## Build
Open in Android Studio with JDK 17 and Android SDK 35. The Gradle project currently relies on an installed Gradle 8.9 for CI because Gradle wrapper files have not yet been added.

## Next steps
1. Run the Android CI build and fix any compilation errors.
2. Add unit tests for ledger invariants and persistence.
3. Add local authentication/app lock and encryption considerations.
4. Implement Supabase auth and sync with server-side authorization.
5. Add immutable reversals, audit logs, reporting, reminders, backup/restore, and release packaging.
