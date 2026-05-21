# Fan Polls — Firestore setup

Used on **Home** (arena polls), **Fan Polls** screen, and **Match Hub** (match-specific polls).

| Collection | `fan_polls` |
|------------|-------------|
| Document ID | Becomes `pollId` (e.g. `FP_101`) |

| Screen | Which polls load |
|--------|------------------|
| Home / Fan Polls | `matchId` is **empty** |
| Match Hub | `matchId` equals hub id (e.g. `M_RCB_KKR_01`) |

Votes still update **locally** after load (same as trending).

---

## Field reference

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| question | string | yes | Poll question |
| category | string | yes | e.g. Thala Corner |
| options | **array** of strings | yes | 2–4 choices |
| voteCounts | **array** of numbers | yes | Same length as options |
| matchId | string | yes | `""` for arena; match id for hub |
| sortOrder | number | yes | Display order |
| isActive | boolean | yes | `true` to show |

---

## Sample documents

### `FP_101` (Home / Fan Polls — 3 options)

| Field | Value |
|-------|-------|
| question | Will MSD push himself up the batting order during run chases this week? |
| category | Thala Corner |
| options | `["Yes, definitely needed", "No, stay at No. 8", "Depends on required run rate"]` |
| voteCounts | `[139200, 33600, 67200]` |
| matchId | *(empty string)* |
| sortOrder | 1 |
| isActive | true |

In Console: add field **options** type array, add each string; **voteCounts** type array, add each number.

### `FP_102` (arena, 2 options)

| Field | Value |
|-------|-------|
| question | Should RCB bench an overseas batsman to bring in an extra specialist death bowler? |
| category | Squad Strategy |
| options | `["Yes, bowling is leaking runs", "No, back the batting line-up"]` |
| voteCounts | `[82080, 31920]` |
| matchId | *(empty)* |
| sortOrder | 2 |
| isActive | true |

### `MH_p1` (Match Hub)

| Field | Value |
|-------|-------|
| question | Who wins the toss? |
| category | Match Hub |
| options | `["RCB", "KKR"]` |
| voteCounts | `[7440, 4960]` |
| matchId | `M_RCB_KKR_01` |
| sortOrder | 3 |
| isActive | true |

### `MH_p2` (Match Hub)

| Field | Value |
|-------|-------|
| question | How many maximum sixes will be hit? |
| category | Match Hub |
| options | `["0-5", "6-12", "13+"]` |
| voteCounts | `[2670, 4450, 1780]` |
| matchId | `M_RCB_KKR_01` |
| sortOrder | 4 |
| isActive | true |

---

## Security rules

```javascript
match /fan_polls/{id} {
  allow read: if true;
  allow write: if false;
}
```

---

## Seed script

```bash
cd scripts/seed-firestore
npm run seed
```

---

## Verify

1. **Home** → Fan Polls section (FP_101, FP_102)
2. **Fan Polls** screen → same arena polls
3. **Match Hub** → MH_p1, MH_p2 when `matchId` matches
4. Logcat: **`FirestoreFanPolls`** → `Parsed fan poll count: 4`

---

## Tips

| Issue | Fix |
|-------|-----|
| Empty on Home | `matchId` must be **empty string**, not missing |
| Empty on Match Hub | `matchId` must match `MatchConstants` hub id exactly |
| Parse fails | `voteCounts` must be numbers; same length as `options` |
| `options` wrong type | Use array in Console, not a single string |
