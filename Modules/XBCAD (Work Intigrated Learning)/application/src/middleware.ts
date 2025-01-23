import { clerkMiddleware, createRouteMatcher } from '@clerk/nextjs/server'
import { NextFetchEvent, NextRequest, NextResponse } from 'next/server'
import { getUserById } from './server/actions'

// export function middleware(request: NextRequest, event: NextFetchEvent) {
//   if (request.method === 'OPTIONS') {
//     const origin = request.headers.get('origin')
//     return new Response(null, {
//       status: 200,
//       headers: {
//         'Access-Control-Allow-Origin': origin || '*',
//         'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS',
//         'Access-Control-Allow-Headers':
//           'Content-Type, Authorization,  Accept, Accept-Version, Content-Length, Content-MD5, Date, X-Api-Version',
//         'Access-Control-Max-Age': '86400',
//       },
//     })
//   }

//   return auth(request, event)
// }

const isProtectedRoute = createRouteMatcher([
  '/(.*)',
  '/admin(.*)',
  '/lesson-booking(.*)',
])

// export default clerkMiddleware(async (auth, req) => {
//   if (isProtectedRoute(req)) await auth.protect()
// })

export default clerkMiddleware(async (auth, req) => {
  const { userId, redirectToSignIn, protect } = await auth()

  if (!userId && isProtectedRoute(req)) await protect()

  const user = await getUserById(userId!)
  const role = user.publicMetadata.userRole
  const path = req.nextUrl.pathname

  if (path.startsWith('/admin')) {
    if (role.id === 1 && role.role === 'Admin') {
      return NextResponse.next()
    }

    return NextResponse.redirect(new URL('/lesson-booking', req.url))
  }

  if (path == '/') {
    if (role.id === 1 && role.role === 'Admin') {
      return NextResponse.redirect(new URL('/admin', req.url))
    }

    return NextResponse.redirect(new URL('/lesson-booking', req.url))
  }
})

export const config = {
  matcher: [
    // Skip Next.js internals and all static files, unless found in search params
    '/((?!_next|[^?]*\\.(?:html?|css|js(?!on)|jpe?g|webp|png|gif|svg|ttf|woff2?|ico|csv|docx?|xlsx?|zip|webmanifest)).*)',
    // Always run for API routes
    '/(api|trpc)(.*)',
  ],
}
