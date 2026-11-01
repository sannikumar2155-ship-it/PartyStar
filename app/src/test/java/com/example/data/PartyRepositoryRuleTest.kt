package com.example.data

import com.example.base.FirestoreEmulatorTestBase
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class PartyRepositoryRuleTest : FirestoreEmulatorTestBase() {

  @Test
  fun saveProfile_authenticatedUser_createsDocumentSuccessfully() = runBlocking {
    val uid = signInTestUser(ALICE_EMAIL)
    val repo = FirestoreRepository(firestore)

    val profile = UserProfile(
      id = uid,
      name = "AliceParty",
      avatar = "👑",
      loginMethod = LoginMethod.GMAIL,
      coins = 60000L,
      diamonds = 1200L,
      vipLevel = 2
    )

    withTimeout(DEFAULT_TIMEOUT_MS) {
      repo.saveUserProfile(profile)
    }

    val fetched = withTimeout(DEFAULT_TIMEOUT_MS) {
      repo.observeUserProfile(uid).first()
    }

    assertNotNull(fetched)
    assertEquals("AliceParty", fetched?.name)
    assertEquals(60000L, fetched?.coins)
  }

  @Test
  fun getProfile_crossUserAccess_failsWithPermissionDenied() = runBlocking {
    val aliceUid = signInTestUser(ALICE_EMAIL)
    val aliceRepo = FirestoreRepository(firestore)

    withTimeout(DEFAULT_TIMEOUT_MS) {
      aliceRepo.saveUserProfile(
        UserProfile(
          id = aliceUid,
          name = "AliceSecret",
          avatar = "🌸",
          loginMethod = LoginMethod.GMAIL
        )
      )
    }

    // Sign in as Bob
    signInTestUser(BOB_EMAIL)
    val bobRepo = FirestoreRepository(firestore)

    try {
      withTimeout(FLOW_TIMEOUT_MS) {
        bobRepo.observeUserProfile(aliceUid).first()
      }
      fail("Expected exception for cross-user profile access")
    } catch (e: Exception) {
      val fsEx = checkNotNull(e.unwrapFirestoreException()) {
        "Expected nested FirebaseFirestoreException, got $e"
      }
      assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, fsEx.code)
    }
  }

  @Test
  fun observeRooms_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
    auth.signOut()
    val repo = FirestoreRepository(firestore)

    try {
      withTimeout(FLOW_TIMEOUT_MS) {
        repo.observeRooms().first()
      }
      fail("Expected exception for unauthenticated rooms access")
    } catch (e: Exception) {
      val fsEx = checkNotNull(e.unwrapFirestoreException()) {
        "Expected nested FirebaseFirestoreException, got $e"
      }
      assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, fsEx.code)
    }
  }

  private companion object {
    const val ALICE_EMAIL = "alice@partystar.com"
    const val BOB_EMAIL = "bob@partystar.com"
    const val DEFAULT_TIMEOUT_MS = 6000L
    const val FLOW_TIMEOUT_MS = 4000L
  }
}
