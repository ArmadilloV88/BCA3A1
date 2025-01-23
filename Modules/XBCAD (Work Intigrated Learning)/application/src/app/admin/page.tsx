import {
  Tabs,
  TabsContent,
  TabsList,
  TabsTrigger,
} from '@/components/shadcn-ui/tabs'
import { DataTable } from '@/components/shadcn-ui/data-table'
import UsersTable from '@/components/ui/users-table'
import HorsesTable from '@/components/ui/horses-table'
import AddressesTable from '@/components/ui/addresses-table'
import ContactDetailsTable from '@/components/ui/contact-details-table'
import PaymentDetailsTable from '@/components/ui/payment-details-table'

export default function page() {
  return (
    <div className="w-full">
      <Tabs defaultValue="users" className="flex flex-col">
        <TabsList className="mx-auto">
          <TabsTrigger value="users">Users</TabsTrigger>
          {/* <TabsTrigger value="horses">Horses</TabsTrigger> */}
          <TabsTrigger value="addresses">Addresses</TabsTrigger>
          <TabsTrigger value="contact_details">Contact Details</TabsTrigger>
          <TabsTrigger value="payment_details">Payment Details</TabsTrigger>
        </TabsList>
        <TabsContent value="users">
          <UsersTable />
        </TabsContent>
        {/* <TabsContent value="horses">
          <HorsesTable />
        </TabsContent> */}
        <TabsContent value="addresses">
          <AddressesTable />
        </TabsContent>
        <TabsContent value="contact_details">
          <ContactDetailsTable />
        </TabsContent>
        <TabsContent value="payment_details">
          <PaymentDetailsTable />
        </TabsContent>
      </Tabs>
    </div>
  )
}

// Test data for table
type Payment = {
  id: string
  amount: number
  status: 'pending' | 'processing' | 'success' | 'failed'
  email: string
}

export const payments: Payment[] = [
  {
    id: '728ed52f',
    amount: 100,
    status: 'pending',
    email: 'm@example.com',
  },
  {
    id: '489e1d42',
    amount: 125,
    status: 'processing',
    email: 'example@gmail.com',
  },
  // ...
]
