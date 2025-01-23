import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import a from 'axios'
import { toast } from 'sonner'
import {
  createUser,
  deleteUser,
  getUserById,
  getUsers,
  updateUser,
} from '@/server/actions'
import { auth, currentUser, User } from '@clerk/nextjs/server'
import { useAuth } from '@clerk/nextjs'
import { UserParams } from '@/models/user-params'

const axios = a.create({
  baseURL: 'https://api/clerk.com/v1',
})

export default function useUsers(userId: string | undefined = undefined) {
  const queryClient = useQueryClient()

  const users = useQuery({
    queryKey: ['users'],
    queryFn: async () => {
      const res = await getUsers()

      return JSON.parse(JSON.stringify(res)) as User[]
    },
    refetchInterval: 1000 * 60 * 2, // refetch every 2 mins
    refetchIntervalInBackground: false,
  })

  

  const userCreation = useMutation({
    mutationKey: ['create-user'],
    mutationFn: async (newUser: UserParams) => {
      await createUser(newUser)
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['users'] })

      userCreation.reset()

      toast.success('Successfully Added User')
    },
    onError: (error) => {
      toast.error(`An error occurred: ${error.message}`)
    },
  })

  const userUpdation = useMutation({
    mutationKey: ['update-user'],
    mutationFn: async (userToUpdate: UserParams) => {
      await updateUser(userToUpdate)
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['users'] })

      userUpdation.reset()

      toast.success('Successfully Updated User')
    },
    onError: (error) => {
      toast.error(`An error occurred: ${error.message}`)
    },
  })

  const userDeletion = useMutation({
    mutationKey: ['delete-user'],
    mutationFn: async (userIdToDelete: string) => {
      // await axios.delete(`/user?id=eq.${userToDelete.id}`)

      await deleteUser(userIdToDelete)
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['users'] })

      userDeletion.reset()

      toast.success('Successfully Deleted User')
    },
    onError: (error) => {
      toast.error(`An error occurred: ${error.message}`)
    },
  })

  return {
    users,
    // userById,
    userCreation,
    userUpdation,
    userDeletion,
  }
}
