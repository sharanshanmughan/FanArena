# Match Hub — Live discussion (user-driven)

Real-time chat per match. Any signed-in user (Google or Guest) can read and post; all devices see updates instantly.

## Firestore structure

```
match_discussions/
  └── M_RCB_KKR_01/          ← matchId (same as Match Hub / fan poll matchId)
        └── comments/
              └── {autoId}/
                    userId: string
                    username: string
                    avatarUrl: string
                    text: string
                    supportTeamBadge: string
                    createdAt: number (epoch ms)
```

You do **not** need to create the parent document `M_RCB_KKR_01` manually — the first comment creates the path.

---

## Security rules

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /match_discussions/{matchId}/comments/{commentId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null
        && request.resource.data.userId == request.auth.uid
        && request.resource.data.text is string
        && request.resource.data.text.size() > 0
        && request.resource.data.text.size() <= 500;
      allow update, delete: if false;
    }
  }
}
```

---

## How it works in the app

| Step | Behavior |
|------|----------|
| Open Match Hub → **Discussion** | `addSnapshotListener` loads comments ordered by `createdAt` |
| User types + Send | Writes new doc with Firebase Auth `uid` + `displayName` |
| Other users | See the comment within ~1s (real-time listener) |
| Not signed in | Error: "Sign in to join the discussion." |

---

## Optional seed comments (Console)

1. Collection: `match_discussions`
2. Document ID: `M_RCB_KKR_01`
3. Subcollection: `comments`
4. Add document (auto ID) with:

| Field | Type | Example |
|-------|------|---------|
| userId | string | seed_user_1 |
| username | string | KingKohli_Fan |
| avatarUrl | string | *(empty)* |
| text | string | Kohli scoring a century tonight! |
| supportTeamBadge | string | RCB |
| createdAt | number | `1700000000000` |

Or run `npm run seed` in `scripts/seed-firestore` (includes sample comments).

---

## Verify

1. Sign in (Google or Guest)
2. Open **Match Hub** → **Discussion**
3. Post a message → appears for you immediately
4. Open on another emulator/device (same account or another) → same messages
5. Logcat: **`FirestoreDiscussion`** → `Live comments for M_RCB_KKR_01: N`

---

## Future improvements

| Feature | Approach |
|---------|----------|
| Team picker for badge | Dropdown in UI → `supportTeamBadge` field |
| Profile photos | Load `photoUrl` from `users` collection |
| Reactions | Subcollection or `reactions` map on comment |
| Moderation | Cloud Function + `isHidden` flag |
| Report abuse | Callable function + admin console |

---

## Code map

| File | Role |
|------|------|
| `DiscussionFirestoreDataSource.kt` | Real-time listen + post |
| `DiscussionRepository.kt` | Auth + post API |
| `MatchHubViewModel.kt` | `discussionComments` StateFlow |
| `MatchHubScreen.kt` | Discussion tab UI |
