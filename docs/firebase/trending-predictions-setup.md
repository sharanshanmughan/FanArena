# Trending Predictions — Firestore setup

Used on **Home** (carousel) and **Trending Forecasts** screen.

| Collection | `trending_predictions` |
|------------|------------------------|
| Document ID | Becomes `predictionId` (e.g. `TP_01`) |

Votes in the app still update **locally** after load; Firestore supplies questions, options, and starting vote counts.

---

## 1. Create collection

Firebase Console → Firestore → **Start collection** → ID: `trending_predictions`

---

## 2. Add documents

### `TP_01`

| Field | Type | Value |
|-------|------|-------|
| category | string | Match Multiplier |
| question | string | Which team will score more than 70 runs in their Powerplay? |
| option1Name | string | SRH Arena |
| option2Name | string | MI Camp |
| voteCount1 | **number** | 65970 |
| voteCount2 | **number** | 23130 |
| trendingTag | string | 🔥 ACCELERATING |
| matchId | string | *(leave empty or omit)* |
| sortOrder | **number** | 1 |
| isActive | boolean | true |

### `TP_02`

| Field | Type | Value |
|-------|------|-------|
| category | string | Player Performance |
| question | string | Who will pick up more wickets during death overs tonight? |
| option1Name | string | J. Bumrah |
| option2Name | string | P. Cummins |
| voteCount1 | number | 68320 |
| voteCount2 | number | 43680 |
| trendingTag | string | 👑 HEAD-TO-HEAD |
| sortOrder | number | 2 |
| isActive | boolean | true |

### `TP_03`

| Field | Type | Value |
|-------|------|-------|
| category | string | Boundaries |
| question | string | Total match sixes boundary count estimation baseline: |
| option1Name | string | Over 15.5 Sixes |
| option2Name | string | Under 15.5 Sixes |
| voteCount1 | number | 18810 |
| voteCount2 | number | 15390 |
| sortOrder | number | 3 |
| isActive | boolean | true |

### `TP_MATCH` (links to match prediction screen)

| Field | Type | Value |
|-------|------|-------|
| category | string | Match Multiplier |
| question | string | Who wins tonight — RCB or KKR? |
| option1Name | string | RCB |
| option2Name | string | KKR |
| voteCount1 | number | 52400 |
| voteCount2 | number | 36600 |
| trendingTag | string | 🏟 MATCH NIGHT |
| matchId | string | M_RCB_KKR_01 |
| sortOrder | number | 4 |
| isActive | boolean | true |

---

## 3. Security rules

```javascript
match /trending_predictions/{id} {
  allow read: if true;
  allow write: if false;
}
```

---

## 4. Seed script

```bash
cd scripts/seed-firestore
npm run seed
```

Includes `trending_predictions` from `docs/firebase/sample-data/trending-predictions.json`.

---

## 5. Verify

1. Rebuild and open **Home** → Trending Predictions section shows cards.
2. Tap **See all** → Trending Forecasts with category filters.
3. Logcat tag **`FirestoreTrending`**: `Parsed trending count: 4`

---

## Field tips

| Mistake | Fix |
|---------|-----|
| Empty list | Add `isActive: true` on every document |
| Parse fails | `voteCount1` / `voteCount2` / `sortOrder` must be **numbers**, not strings |
| Filter shows nothing | `category` must match exactly (e.g. `Player Performance`) |
| Match card does nothing | Set `matchId` to your hub match id |

---

## Code map

| File | Role |
|------|------|
| `TrendingPredictionsFirestoreDataSource.kt` | Firestore read |
| `TrendingPredictionDocument.kt` | Document model |
| `TrendingPredictionsRepository.kt` | UI + local votes |
| `TrendingVotesRepository.kt` | In-memory vote updates |
