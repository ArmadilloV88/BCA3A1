// The following code was adapted from a v0 staticGenerationAsyncStorage
// Link: https://v0.dev/
'use client'

import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import * as z from 'zod'
import { Button } from '@/components/shadcn-ui/button'
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/shadcn-ui/form'
import { Input } from '@/components/shadcn-ui/input'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/shadcn-ui/select'

import {
  Drawer,
  DrawerTrigger,
  DrawerClose,
  DrawerContent,
  DrawerHeader,
  DrawerFooter,
  DrawerTitle,
  DrawerDescription,
} from '@/components/shadcn-ui/drawer'
import { CirclePlus, Loader2, Pencil, X } from 'lucide-react'
import { ReactNode } from 'react'
import { UseMutationResult } from '@tanstack/react-query'
import useSkillLevels from '@/hooks/useSkillLevels'
import useUserRoles from '@/hooks/useUserRoles'
import { User } from '@clerk/nextjs/server'
import { UserParams } from '@/models/user-params'

const userCreationSchema = z.object({
  // id: z
  //   .number()
  //   .int()
  //   .positive()
  //   .or(z.string().regex(/^\d+$/).transform(Number)),
  userRoleId: z.number({ required_error: 'A user role is required' }).int(),
  skillLevelId: z.number({ required_error: 'A skill level is required' }).int(),
  firstName: z
    .string()
    .min(3, { message: 'Name must be at least 3 characters long' })
    .max(50, { message: 'Name must not exceed 50 characters' }),
  lastName: z
    .string()
    .min(3, { message: 'Name must be at least 3 characters long' })
    .max(50, { message: 'Name must not exceed 50 characters' }),
  emailAddress: z
    .string()
    .email({ message: 'Invalid email address' })
    .transform((val) => [val]),

  /*  .array() */
  phoneNumber: z.string().min(1, { message: 'A phone number is required' }),
  // .array()
  // .regex(/^\+?[1-9]\d{1,14}$/, { message: 'Invalid phone number format' })
  // .optional(),
  password: z
    .string({
      required_error: 'Password is required',
    })
    .min(8, 'Password must be at least 8 characters long'),
  // profilePicture: z.object({
  //   url: z.string().url({ message: 'Invalid URL format' }),
  //   alt: z.string().min(1, { message: 'Alt text is required' }),
  // }),
})

const userUpdationSchema = userCreationSchema.partial({ password: true })

interface Props {
  mode?: 'create' | 'update'
  userToUpdate?: User
  trigger?: ReactNode
  isOpen?: boolean
  onOpenChanged?: (open: boolean) => void
  itemAction: UseMutationResult<void, Error, UserParams, unknown>
}

export default function UserFormDrawer({
  mode = 'create',
  userToUpdate,
  trigger,
  isOpen,
  onOpenChanged,
  itemAction,
}: Props) {
  const { skillLevels } = useSkillLevels()
  const { userRoles } = useUserRoles()

  const form = useForm<UserParams>({
    resolver: zodResolver(
      mode === 'create' ? userCreationSchema : userUpdationSchema
    ),
    // Although there is an error on the following line, everything still works fine.
    // Suprisigly, the fix to the following type error causes the app to not work as intended
    // @ts-expect-error
    defaultValues:
      mode === 'update' && userToUpdate
        ? {
            userRoleId: userToUpdate?.publicMetadata?.userRole?.id,
            skillLevelId: userToUpdate?.publicMetadata?.skillLevel?.id,
            firstName: userToUpdate?.firstName || undefined,
            lastName: userToUpdate?.lastName || undefined,
            emailAddress: userToUpdate?.emailAddresses[0]?.emailAddress,
            phoneNumber: userToUpdate?.publicMetadata?.phoneNumber,
          }
        : {
            userRoleId: 3,
            skillLevelId: 1,
            firstName: '',
            lastName: '',
          },
  })

  const onSubmit = (data: UserParams) => {
    // fetch the appropriate skill level and user role based on the selected id
    data.userRole = userRoles.data?.find((val) => val.id === data.userRoleId)!
    data.skillLevel = skillLevels.data?.find(
      (val) => val.id === data.skillLevelId
    )!

    if (mode === 'create') itemAction.mutate(data)
    else if (userToUpdate) {
      data.userId = userToUpdate?.id

      itemAction.mutate(data)
    }

    form.reset()

    if (itemAction.isIdle && !itemAction.isError && onOpenChanged) {
      onOpenChanged(false)
    }
  }

  return (
    <Drawer direction="right" open={isOpen} onOpenChange={onOpenChanged}>
      <DrawerTrigger asChild>
        {trigger ? (
          trigger
        ) : (
          <Button size="sm" className="hidden h-8 lg:flex">
            <CirclePlus className="mr-1 size-4" />
            Add User
          </Button>
        )}
      </DrawerTrigger>
      <DrawerContent
        className="fixed inset-x-auto bottom-2 right-2 top-2 mt-0 flex w-[350px] rounded-md outline-none after:!content-none"
        style={
          {
            '--initial-transform': 'calc(100% + 8px)',
          } as React.CSSProperties
        }
      >
        <DrawerHeader>
          <DrawerTitle>
            {mode === 'create' ? 'Add A' : 'Update'} User
          </DrawerTitle>
          <DrawerDescription>
            Fill in the following fields to{' '}
            {mode === 'create'
              ? 'add a new user to the application'
              : "update a user's details"}
          </DrawerDescription>
        </DrawerHeader>

        <div className="h-full overflow-auto p-5">
          <Form {...form}>
            <form className="space-y-5">
              {/* <FormField
                control={form.control}
                name="id"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>ID</FormLabel>
                    <FormControl>
                      <Input
                        type="number"
                        {...field}
                        onChange={(e) =>
                          field.onChange(parseInt(e.target.value))
                        }
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              /> */}

              <div className="flex items-center justify-between gap-3">
                <FormField
                  control={form.control}
                  name="firstName"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>First Name</FormLabel>
                      <FormControl>
                        <Input {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                <FormField
                  control={form.control}
                  name="lastName"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Last Name</FormLabel>
                      <FormControl>
                        <Input {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
              </div>

              <FormField
                control={form.control}
                name="emailAddress"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Email</FormLabel>
                    <FormControl>
                      <Input
                        readOnly={mode === 'update'}
                        type="email"
                        {...field}
                      />
                    </FormControl>
                    <FormDescription>
                      Note: As of now, a users email cannot be updated! Ensure
                      everything is correct!
                    </FormDescription>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="phoneNumber"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Phone</FormLabel>
                    <FormControl>
                      <Input {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="userRoleId"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>User Role</FormLabel>
                    <Select
                      onValueChange={(value) => field.onChange(parseInt(value))}
                      defaultValue={field.value.toString()}
                    >
                      <FormControl>
                        <SelectTrigger>
                          <SelectValue placeholder="Select user role" />
                        </SelectTrigger>
                      </FormControl>
                      <SelectContent>
                        {userRoles.isLoading ? (
                          <SelectValue placeholder="Loading Roles..." />
                        ) : (
                          userRoles?.data?.map((item) => (
                            <SelectItem
                              key={item.id}
                              value={item.id.toString()}
                            >
                              {item.role}
                            </SelectItem>
                          ))
                        )}
                      </SelectContent>
                    </Select>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="skillLevelId"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Skill Level</FormLabel>
                    {/* <div className="flex items-center space-x-2"> */}
                    <Select
                      onValueChange={(value) => field.onChange(parseInt(value))}
                      defaultValue={field?.value?.toString()}
                    >
                      <FormControl>
                        <SelectTrigger>
                          <SelectValue placeholder="Select skill level" />
                        </SelectTrigger>
                      </FormControl>
                      <SelectContent>
                        {skillLevels?.data?.map((item) => (
                          <SelectItem key={item.id} value={item.id.toString()}>
                            {item.level}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                    {/* {field.value && mode === 'create' && (
                        <Button
                          type="button"
                          variant="ghost"
                          size="icon"
                          onClick={() => field.onChange(undefined)}
                          className="h-8 w-8 p-0"
                        >
                          <X className="h-4 w-4" />
                        </Button>
                      )} */}
                    {/* </div> */}
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="password"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Password</FormLabel>
                    <FormControl>
                      <Input
                        type="password"
                        placeholder="Enter your password"
                        {...field}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              {/* <FormField
              control={form.control}
              name="profilePicture.url"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Profile Picture URL</FormLabel>
                  <FormControl>
                    <Input {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="profilePicture.alt"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Profile Picture Alt Text</FormLabel>
                  <FormControl>
                    <Input {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            /> */}
            </form>
          </Form>
        </div>

        <DrawerFooter>
          <Button
            onClick={form.handleSubmit(onSubmit)}
            disabled={itemAction.isPending}
          >
            {mode === 'create' ? (
              itemAction.isPending ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  <span>Adding User...</span>
                </>
              ) : (
                <>
                  <CirclePlus className="mr-1 size-4" />
                  <span>Add User</span>
                </>
              )
            ) : itemAction.isPending ? (
              <>
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                <span>Updating User...</span>
              </>
            ) : (
              <>
                <Pencil className="mr-1 size-4" />
                <span>Update User</span>
              </>
            )}
          </Button>
          <DrawerClose asChild>
            <Button variant="outline">Cancel</Button>
          </DrawerClose>
        </DrawerFooter>
      </DrawerContent>
    </Drawer>
  )
}
