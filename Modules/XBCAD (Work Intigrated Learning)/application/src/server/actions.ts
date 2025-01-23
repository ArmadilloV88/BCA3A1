'use server'

import { clerkClient } from '@/lib/clerk-client'
import { UserParams } from '@/models/user-params'
import { /* clerkClient ,*/ currentUser, User } from '@clerk/nextjs/server'

// Handle errors

export async function getUsers() {
  const response = await clerkClient.users.getUserList()

  // The following line was adapted from stackoverflow.com
  // Author: amir rasooli (https://stackoverflow.com/users/10708719/amir-rasooli)
  // Link: https://stackoverflow.com/questions/77091418/warning-only-plain-objects-can-be-passed-to-client-components-from-server-compo
  return JSON.parse(JSON.stringify(response.data))
}

export async function getCurrentUser() {
  const response = await currentUser()

  // The following line was adapted from stackoverflow.com
  // Author: amir rasooli (https://stackoverflow.com/users/10708719/amir-rasooli)
  // Link: https://stackoverflow.com/questions/77091418/warning-only-plain-objects-can-be-passed-to-client-components-from-server-compo
  return JSON.parse(JSON.stringify(response)) as User
}

export async function getUserById(userId: string) {
  const response = await clerkClient.users.getUser(userId)

  // The following line was adapted from stackoverflow.com
  // Author: amir rasooli (https://stackoverflow.com/users/10708719/amir-rasooli)
  // Link: https://stackoverflow.com/questions/77091418/warning-only-plain-objects-can-be-passed-to-client-components-from-server-compo
  return JSON.parse(JSON.stringify(response))
}

export async function createUser(userData: UserParams) {
  const newUser = {
    firstName: userData.firstName,
    lastName: userData.lastName,
    emailAddress: userData.emailAddress,
    publicMetadata: {
      skillLevel: userData.skillLevel,
      userRole: userData.userRole,
      // The normal phone number was giving wayyyy to many problems for our limited time
      phoneNumber: userData.phoneNumber,
    },
    password: userData.password,
    skipPasswordChecks: true,
    skipPasswordRequirement: true,
  }

  await clerkClient.users.createUser(newUser)
}

export async function updateUser(userToUpdate: UserParams) {
  const updatedUser = {
    firstName: userToUpdate.firstName,
    lastName: userToUpdate.lastName,
    emailAddress: userToUpdate.emailAddress,
    publicMetadata: {
      skillLevel: userToUpdate.skillLevel,
      userRole: userToUpdate.userRole,
      // The normal phone number was giving wayyyy to many problems for our limited time
      phoneNumber: userToUpdate.phoneNumber,
    },
    password: userToUpdate.password,
    skipPasswordChecks: true,
    skipPasswordRequirement: true,
  }

  await clerkClient.users.updateUser(userToUpdate.userId!, updatedUser)
}

export async function deleteUser(userId: string) {
  await clerkClient.users.deleteUser(userId)
}
