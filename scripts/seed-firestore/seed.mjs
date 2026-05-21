/**
 * One-time Firestore seed for Home + Today's Matches + leaderboard.
 *
 * Setup:
 *   1. Firebase Console → Project settings → Service accounts → Generate new private key
 *   2. Save as scripts/seed-firestore/serviceAccountKey.json (never commit)
 *   3. cd scripts/seed-firestore && npm install && npm run seed
 */
import { readFileSync } from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";
import admin from "firebase-admin";

const __dirname = dirname(fileURLToPath(import.meta.url));
const keyPath = join(__dirname, "serviceAccountKey.json");
const matchesPath = join(__dirname, "../../docs/firebase/sample-data/matches.json");
const leaderboardPath = join(__dirname, "../../docs/firebase/sample-data/home-leaderboard.json");
const trendingPath = join(__dirname, "../../docs/firebase/sample-data/trending-predictions.json");
const fanPollsPath = join(__dirname, "../../docs/firebase/sample-data/fan-polls.json");
const discussionSeedPath = join(__dirname, "../../docs/firebase/sample-data/discussion-seed.json");

const serviceAccount = JSON.parse(readFileSync(keyPath, "utf8"));
const matches = JSON.parse(readFileSync(matchesPath, "utf8"));
const leaderboard = JSON.parse(readFileSync(leaderboardPath, "utf8"));
const trending = JSON.parse(readFileSync(trendingPath, "utf8"));
const fanPolls = JSON.parse(readFileSync(fanPollsPath, "utf8"));
const discussionSeed = JSON.parse(readFileSync(discussionSeedPath, "utf8"));

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

const db = admin.firestore();

async function setCollection(collection, documentsById) {
  const batch = db.batch();
  for (const [id, data] of Object.entries(documentsById)) {
    batch.set(db.collection(collection).doc(id), data, { merge: true });
  }
  await batch.commit();
  console.log(`Seeded ${Object.keys(documentsById).length} docs → ${collection}`);
}

await setCollection("matches", matches);
await setCollection("leaderboard_global", leaderboard);
await setCollection("trending_predictions", trending);
await setCollection("fan_polls", fanPolls);

const matchId = discussionSeed.matchId;
for (const [commentId, data] of Object.entries(discussionSeed.comments)) {
  await db
    .collection("match_discussions")
    .doc(matchId)
    .collection("comments")
    .doc(commentId)
    .set(data, { merge: true });
}
console.log(`Seeded ${Object.keys(discussionSeed.comments).length} discussion comments → match_discussions/${matchId}/comments`);

console.log("Done.");
process.exit(0);
