# Home screen — Firestore setup guide

The Home screen reads two Firestore collections:

| Collection | Purpose |
|------------|---------|
| `matches` | Today's matches carousel (`showOnHome = true`) |
| `leaderboard_global` | Top 3 users by `points` (descending) |

Project ID (from `app/google-services.json`): **simpleroomapp-46c79**

---

## 1. Enable Firestore

1. Open [Firebase Console](https://console.firebase.google.com/) → project **simpleroomapp-46c79**.
2. Go to **Build** → **Firestore Database**.
3. Click **Create database**.
4. Choose **Start in test mode** while developing (tighten rules before release; see section 5).
5. Pick a region close to your users (e.g. `asia-south1` for India).

---

## 2. Add sample matches

Use the full **`matches`** schema (Home + Today's Matches share one collection).

**Recommended:** run the seed script (see [matches-firestore-setup.md](./matches-firestore-setup.md#seed-script-automated)).

**Manual:** follow [matches-firestore-setup.md](./matches-firestore-setup.md) for all fields on `match_1` … `match_4`.

**Notes**

- Document ID becomes `Match.id` in the app (e.g. `match_1`).
- `showOnHome: true` → Home carousel; all documents appear on **Today's Matches** sorted by `sortOrder`.
- `status` must be `UPCOMING`, `LIVE`, or `COMPLETED` (uppercase).

---

## 3. Add sample leaderboard

1. **Start collection** (or add collection): `leaderboard_global`
2. Add three documents (IDs are arbitrary; ranking is by `points` only).

### Document `lb_1`

| Field | Type | Value |
|-------|------|-------|
| `username` | string | `CricketGuru` |
| `avatarUrl` | string | `avatar_1` |
| `points` | number | `2450` |

### Document `lb_2`

| Field | Type | Value |
|-------|------|-------|
| `username` | string | `Hitman_Fan` |
| `avatarUrl` | string | `avatar_2` |
| `points` | number | `2310` |

### Document `lb_3`

| Field | Type | Value |
|-------|------|-------|
| `username` | string | `Thala_07` |
| `avatarUrl` | string | `avatar_3` |
| `points` | number | `2290` |

**Notes**

- `rank` is **not** stored in Firestore. The app sorts by `points` descending and assigns ranks 1, 2, 3.
- To change podium order, only update `points`.

---

## 4. JSON reference (for scripts or documentation)

Sample files in the repo:

- `docs/firebase/sample-data/matches.json`
- `docs/firebase/sample-data/home-leaderboard.json`

The Firebase Console UI adds documents field-by-field; use these JSON files if you seed data with the [Firebase CLI](https://firebase.google.com/docs/firestore/manage-data/add-data#firebase-cli) or Admin SDK later.

---

## 5. Security rules (development)

In Firestore → **Rules**, you can use read-only public data for home while you test:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /matches/{matchId} {
      allow read: if true;
      allow write: if false;
    }
    match /leaderboard_global/{entryId} {
      allow read: if true;
      allow write: if false;
    }
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

Before production, restrict `read` to signed-in users if needed: `allow read: if request.auth != null;`

---

## 6. Verify in the app

1. Sign in (Google or guest) so Firebase is initialized.
2. Open the **Home** tab.
3. You should see the two matches and the top-3 leaderboard from Firestore.
4. If you see an error:
   - Confirm collection names are exactly `matches` and `leaderboard_global`.
   - Confirm field names match the table above (case-sensitive).
   - Check **Logcat** for `FirebaseFirestore` errors.
   - For leaderboard, Firestore auto-creates a single-field index on `points`; if prompted in Console, create the suggested index.

---

## 7. Adding more data later

| Action | How |
|--------|-----|
| New home match | Add document to `matches` with `showOnHome: true` |
| Remove from home only | Set `showOnHome: false` on that document |
| New leaderboard player | Add document to `leaderboard_global` with `username`, `avatarUrl`, `points` |
| Change top 3 | Adjust `points`; app always loads top 3 by score |

---

## Code map (Android)

| Layer | File |
|-------|------|
| Firestore reads | `core/data/remote/firebase/MatchesFirestoreDataSource.kt` |
| Repository | `core/data/repository/HomeRepository.kt` |
| UI state | `feature/home/HomeUiState.kt` |
| Constants | `core/constants/FirebaseConstants.kt` |
