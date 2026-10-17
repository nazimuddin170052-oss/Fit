const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

// --- UNATHENTICATED TESTS ---

test("Unauthenticated user: cannot read user profile", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
});

test("Unauthenticated user: cannot create user profile", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).set({
    userId: ALICE_UID,
    name: "Alice",
    createdAt: new Date(),
  }));
});

// --- USER PROFILE TESTS ---

test("Authenticated user: can create own profile with valid schema", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).set({
    userId: ALICE_UID,
    name: "Alice",
    age: 28,
    heightCm: 172,
    currentWeightKg: 74.0,
    goalWeightKg: 65.0,
    dailyCalorieGoal: 2200,
    createdAt: new Date(),
  }));
});

test("Authenticated user: cannot create another user's profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).set({
    userId: BOB_UID,
    name: "Bob",
    createdAt: new Date(),
  }));
});

test("Authenticated user: cannot read another user's profile", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(BOB_UID).set({
      userId: BOB_UID,
      name: "Bob",
      createdAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).get());
});

// --- MEALS SUBCOLLECTION TESTS ---

test("Authenticated user: can add meal to own subcollection", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("meals").doc("meal_1").set({
    userId: ALICE_UID,
    foodNameEn: "Chicken Biryani",
    foodNameBn: "চিকেন বিরিয়ানি",
    mealType: "Lunch",
    calories: 550,
    protein: 30.5,
    carbs: 65.0,
    fat: 18.0,
    createdAt: new Date(),
  }));
});

test("Authenticated user: cannot add meal with invalid/missing calories", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(ALICE_UID).collection("meals").doc("meal_2").set({
    userId: ALICE_UID,
    foodNameEn: "Invalid Food",
    // missing calories
    createdAt: new Date(),
  }));
});

test("Authenticated user: cannot read another user's meals", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(BOB_UID).collection("meals").doc("bob_meal").set({
      userId: BOB_UID,
      foodNameEn: "Roti and Dal",
      calories: 300,
      createdAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).collection("meals").doc("bob_meal").get());
});

// --- PROGRESS SUBCOLLECTION TESTS ---

test("Authenticated user: can log progress entry in own subcollection", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("progress").doc("entry_1").set({
    userId: ALICE_UID,
    weightKg: 73.5,
    waistInches: 32.5,
    dateLabelEn: "17 Oct 2026",
    dateLabelBn: "১৭ অক্টোবর",
    createdAt: new Date(),
  }));
});

test("Authenticated user: cannot log progress entry for another user", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).collection("progress").doc("entry_2").set({
    userId: BOB_UID,
    weightKg: 75.0,
    createdAt: new Date(),
  }));
});
