; ModuleID = 'marshal_methods.armeabi-v7a.ll'
source_filename = "marshal_methods.armeabi-v7a.ll"
target datalayout = "e-m:e-p:32:32-Fi8-i64:64-v128:64:128-a:0:32-n32-S64"
target triple = "armv7-unknown-linux-android21"

%struct.MarshalMethodName = type {
	i64, ; uint64_t id
	ptr ; char* name
}

%struct.MarshalMethodsManagedClass = type {
	i32, ; uint32_t token
	ptr ; MonoClass klass
}

@assembly_image_cache = dso_local local_unnamed_addr global [350 x ptr] zeroinitializer, align 4

; Each entry maps hash of an assembly name to an index into the `assembly_image_cache` array
@assembly_image_cache_hashes = dso_local local_unnamed_addr constant [694 x i32] [
	i32 2616222, ; 0: System.Net.NetworkInformation.dll => 0x27eb9e => 68
	i32 10166715, ; 1: System.Net.NameResolution.dll => 0x9b21bb => 67
	i32 15721112, ; 2: System.Runtime.Intrinsics.dll => 0xefe298 => 108
	i32 26230656, ; 3: Microsoft.Extensions.DependencyModel => 0x1903f80 => 203
	i32 32687329, ; 4: Xamarin.AndroidX.Lifecycle.Runtime => 0x1f2c4e1 => 270
	i32 34715100, ; 5: Xamarin.Google.Guava.ListenableFuture.dll => 0x211b5dc => 304
	i32 34839235, ; 6: System.IO.FileSystem.DriveInfo => 0x2139ac3 => 48
	i32 39109920, ; 7: Newtonsoft.Json.dll => 0x254c520 => 224
	i32 39485524, ; 8: System.Net.WebSockets.dll => 0x25a8054 => 80
	i32 42639949, ; 9: System.Threading.Thread => 0x28aa24d => 145
	i32 66541672, ; 10: System.Diagnostics.StackTrace => 0x3f75868 => 30
	i32 67008169, ; 11: zh-Hant\Microsoft.Maui.Controls.resources => 0x3fe76a9 => 345
	i32 68219467, ; 12: System.Security.Cryptography.Primitives => 0x410f24b => 124
	i32 72070932, ; 13: Microsoft.Maui.Graphics.dll => 0x44bb714 => 223
	i32 82292897, ; 14: System.Runtime.CompilerServices.VisualC.dll => 0x4e7b0a1 => 102
	i32 101534019, ; 15: Xamarin.AndroidX.SlidingPaneLayout => 0x60d4943 => 288
	i32 117431740, ; 16: System.Runtime.InteropServices => 0x6ffddbc => 107
	i32 120558881, ; 17: Xamarin.AndroidX.SlidingPaneLayout.dll => 0x72f9521 => 288
	i32 122350210, ; 18: System.Threading.Channels.dll => 0x74aea82 => 139
	i32 134690465, ; 19: Xamarin.Kotlin.StdLib.Jdk7.dll => 0x80736a1 => 308
	i32 142721839, ; 20: System.Net.WebHeaderCollection => 0x881c32f => 77
	i32 149972175, ; 21: System.Security.Cryptography.Primitives.dll => 0x8f064cf => 124
	i32 159306688, ; 22: System.ComponentModel.Annotations => 0x97ed3c0 => 13
	i32 165246403, ; 23: Xamarin.AndroidX.Collection.dll => 0x9d975c3 => 244
	i32 176265551, ; 24: System.ServiceProcess => 0xa81994f => 132
	i32 182336117, ; 25: Xamarin.AndroidX.SwipeRefreshLayout.dll => 0xade3a75 => 290
	i32 184328833, ; 26: System.ValueTuple.dll => 0xafca281 => 151
	i32 195452805, ; 27: vi/Microsoft.Maui.Controls.resources.dll => 0xba65f85 => 342
	i32 199333315, ; 28: zh-HK/Microsoft.Maui.Controls.resources.dll => 0xbe195c3 => 343
	i32 205061960, ; 29: System.ComponentModel => 0xc38ff48 => 18
	i32 209399409, ; 30: Xamarin.AndroidX.Browser.dll => 0xc7b2e71 => 242
	i32 220171995, ; 31: System.Diagnostics.Debug => 0xd1f8edb => 26
	i32 230216969, ; 32: Xamarin.AndroidX.Legacy.Support.Core.Utils.dll => 0xdb8d509 => 264
	i32 230752869, ; 33: Microsoft.CSharp.dll => 0xdc10265 => 1
	i32 231409092, ; 34: System.Linq.Parallel => 0xdcb05c4 => 59
	i32 231814094, ; 35: System.Globalization => 0xdd133ce => 42
	i32 246610117, ; 36: System.Reflection.Emit.Lightweight => 0xeb2f8c5 => 91
	i32 254259026, ; 37: Microsoft.AspNetCore.Components.dll => 0xf27af52 => 189
	i32 261689757, ; 38: Xamarin.AndroidX.ConstraintLayout.dll => 0xf99119d => 247
	i32 269079191, ; 39: Xceed.Document.NET => 0x1009d297 => 173
	i32 276479776, ; 40: System.Threading.Timer.dll => 0x107abf20 => 147
	i32 278686392, ; 41: Xamarin.AndroidX.Lifecycle.LiveData.dll => 0x109c6ab8 => 266
	i32 280482487, ; 42: Xamarin.AndroidX.Interpolator => 0x10b7d2b7 => 263
	i32 280992041, ; 43: cs/Microsoft.Maui.Controls.resources.dll => 0x10bf9929 => 314
	i32 291076382, ; 44: System.IO.Pipes.AccessControl.dll => 0x1159791e => 54
	i32 298918909, ; 45: System.Net.Ping.dll => 0x11d123fd => 69
	i32 317674968, ; 46: vi\Microsoft.Maui.Controls.resources => 0x12ef55d8 => 342
	i32 318968648, ; 47: Xamarin.AndroidX.Activity.dll => 0x13031348 => 233
	i32 321597661, ; 48: System.Numerics => 0x132b30dd => 83
	i32 336156722, ; 49: ja/Microsoft.Maui.Controls.resources.dll => 0x14095832 => 327
	i32 342366114, ; 50: Xamarin.AndroidX.Lifecycle.Common => 0x146817a2 => 265
	i32 356389973, ; 51: it/Microsoft.Maui.Controls.resources.dll => 0x153e1455 => 326
	i32 360082299, ; 52: System.ServiceModel.Web => 0x15766b7b => 131
	i32 367780167, ; 53: System.IO.Pipes => 0x15ebe147 => 55
	i32 374914964, ; 54: System.Transactions.Local => 0x1658bf94 => 149
	i32 375677976, ; 55: System.Net.ServicePoint.dll => 0x16646418 => 74
	i32 379916513, ; 56: System.Threading.Thread.dll => 0x16a510e1 => 145
	i32 385762202, ; 57: System.Memory.dll => 0x16fe439a => 62
	i32 392610295, ; 58: System.Threading.ThreadPool.dll => 0x1766c1f7 => 146
	i32 395744057, ; 59: _Microsoft.Android.Resource.Designer => 0x17969339 => 346
	i32 403441872, ; 60: WindowsBase => 0x180c08d0 => 165
	i32 435591531, ; 61: sv/Microsoft.Maui.Controls.resources.dll => 0x19f6996b => 338
	i32 441335492, ; 62: Xamarin.AndroidX.ConstraintLayout.Core => 0x1a4e3ec4 => 248
	i32 442565967, ; 63: System.Collections => 0x1a61054f => 12
	i32 450948140, ; 64: Xamarin.AndroidX.Fragment.dll => 0x1ae0ec2c => 261
	i32 451504562, ; 65: System.Security.Cryptography.X509Certificates => 0x1ae969b2 => 125
	i32 456227837, ; 66: System.Web.HttpUtility.dll => 0x1b317bfd => 152
	i32 459347974, ; 67: System.Runtime.Serialization.Primitives.dll => 0x1b611806 => 113
	i32 465846621, ; 68: mscorlib => 0x1bc4415d => 166
	i32 469710990, ; 69: System.dll => 0x1bff388e => 164
	i32 476646585, ; 70: Xamarin.AndroidX.Interpolator.dll => 0x1c690cb9 => 263
	i32 486930444, ; 71: Xamarin.AndroidX.LocalBroadcastManager.dll => 0x1d05f80c => 276
	i32 498788369, ; 72: System.ObjectModel => 0x1dbae811 => 84
	i32 500358224, ; 73: id/Microsoft.Maui.Controls.resources.dll => 0x1dd2dc50 => 325
	i32 503918385, ; 74: fi/Microsoft.Maui.Controls.resources.dll => 0x1e092f31 => 319
	i32 513247710, ; 75: Microsoft.Extensions.Primitives.dll => 0x1e9789de => 216
	i32 525008092, ; 76: SkiaSharp.dll => 0x1f4afcdc => 225
	i32 526420162, ; 77: System.Transactions.dll => 0x1f6088c2 => 150
	i32 527452488, ; 78: Xamarin.Kotlin.StdLib.Jdk7 => 0x1f704948 => 308
	i32 530272170, ; 79: System.Linq.Queryable => 0x1f9b4faa => 60
	i32 539058512, ; 80: Microsoft.Extensions.Logging => 0x20216150 => 209
	i32 540030774, ; 81: System.IO.FileSystem.dll => 0x20303736 => 51
	i32 545304856, ; 82: System.Runtime.Extensions => 0x2080b118 => 103
	i32 545795345, ; 83: Microsoft.Extensions.Logging.Configuration.dll => 0x20882d11 => 211
	i32 546455878, ; 84: System.Runtime.Serialization.Xml => 0x20924146 => 114
	i32 549171840, ; 85: System.Globalization.Calendars => 0x20bbb280 => 40
	i32 557405415, ; 86: Jsr305Binding => 0x213954e7 => 301
	i32 569601784, ; 87: Xamarin.AndroidX.Window.Extensions.Core.Core => 0x21f36ef8 => 299
	i32 571435654, ; 88: Microsoft.Extensions.FileProviders.Embedded.dll => 0x220f6a86 => 206
	i32 577335427, ; 89: System.Security.Cryptography.Cng => 0x22697083 => 120
	i32 592146354, ; 90: pt-BR/Microsoft.Maui.Controls.resources.dll => 0x234b6fb2 => 333
	i32 601371474, ; 91: System.IO.IsolatedStorage.dll => 0x23d83352 => 52
	i32 605376203, ; 92: System.IO.Compression.FileSystem => 0x24154ecb => 44
	i32 606421715, ; 93: itext.layout => 0x242542d3 => 181
	i32 613668793, ; 94: System.Security.Cryptography.Algorithms => 0x2493d7b9 => 119
	i32 627609679, ; 95: Xamarin.AndroidX.CustomView => 0x2568904f => 253
	i32 627931235, ; 96: nl\Microsoft.Maui.Controls.resources => 0x256d7863 => 331
	i32 639843206, ; 97: Xamarin.AndroidX.Emoji2.ViewsHelper.dll => 0x26233b86 => 259
	i32 643868501, ; 98: System.Net => 0x2660a755 => 81
	i32 662205335, ; 99: System.Text.Encodings.Web.dll => 0x27787397 => 136
	i32 663517072, ; 100: Xamarin.AndroidX.VersionedParcelable => 0x278c7790 => 295
	i32 666292255, ; 101: Xamarin.AndroidX.Arch.Core.Common.dll => 0x27b6d01f => 240
	i32 672442732, ; 102: System.Collections.Concurrent => 0x2814a96c => 8
	i32 683518922, ; 103: System.Net.Security => 0x28bdabca => 73
	i32 688181140, ; 104: ca/Microsoft.Maui.Controls.resources.dll => 0x2904cf94 => 313
	i32 690569205, ; 105: System.Xml.Linq.dll => 0x29293ff5 => 155
	i32 691348768, ; 106: Xamarin.KotlinX.Coroutines.Android.dll => 0x29352520 => 310
	i32 692151471, ; 107: Microsoft.Extensions.Logging.Console.dll => 0x294164af => 212
	i32 693804605, ; 108: System.Windows => 0x295a9e3d => 154
	i32 699345723, ; 109: System.Reflection.Emit => 0x29af2b3b => 92
	i32 700284507, ; 110: Xamarin.Jetbrains.Annotations => 0x29bd7e5b => 305
	i32 700358131, ; 111: System.IO.Compression.ZipFile => 0x29be9df3 => 45
	i32 706645707, ; 112: ko/Microsoft.Maui.Controls.resources.dll => 0x2a1e8ecb => 328
	i32 709557578, ; 113: de/Microsoft.Maui.Controls.resources.dll => 0x2a4afd4a => 316
	i32 720511267, ; 114: Xamarin.Kotlin.StdLib.Jdk8 => 0x2af22123 => 309
	i32 722857257, ; 115: System.Runtime.Loader.dll => 0x2b15ed29 => 109
	i32 731701662, ; 116: Microsoft.Extensions.Options.ConfigurationExtensions => 0x2b9ce19e => 215
	i32 735137430, ; 117: System.Security.SecureString.dll => 0x2bd14e96 => 129
	i32 752232764, ; 118: System.Diagnostics.Contracts.dll => 0x2cd6293c => 25
	i32 755313932, ; 119: Xamarin.Android.Glide.Annotations.dll => 0x2d052d0c => 230
	i32 759454413, ; 120: System.Net.Requests => 0x2d445acd => 72
	i32 762598435, ; 121: System.IO.Pipes.dll => 0x2d745423 => 55
	i32 775507847, ; 122: System.IO.Compression => 0x2e394f87 => 46
	i32 777317022, ; 123: sk\Microsoft.Maui.Controls.resources => 0x2e54ea9e => 337
	i32 789151979, ; 124: Microsoft.Extensions.Options => 0x2f0980eb => 214
	i32 790371945, ; 125: Xamarin.AndroidX.CustomView.PoolingContainer.dll => 0x2f1c1e69 => 254
	i32 804008546, ; 126: Microsoft.AspNetCore.Components.WebView.Maui => 0x2fec3262 => 193
	i32 804715423, ; 127: System.Data.Common => 0x2ff6fb9f => 22
	i32 807930345, ; 128: Xamarin.AndroidX.Lifecycle.LiveData.Core.Ktx.dll => 0x302809e9 => 268
	i32 809851609, ; 129: System.Drawing.Common.dll => 0x30455ad9 => 226
	i32 823281589, ; 130: System.Private.Uri.dll => 0x311247b5 => 86
	i32 830298997, ; 131: System.IO.Compression.Brotli => 0x317d5b75 => 43
	i32 832635846, ; 132: System.Xml.XPath.dll => 0x31a103c6 => 160
	i32 834051424, ; 133: System.Net.Quic => 0x31b69d60 => 71
	i32 843511501, ; 134: Xamarin.AndroidX.Print => 0x3246f6cd => 281
	i32 873119928, ; 135: Microsoft.VisualBasic => 0x340ac0b8 => 3
	i32 877678880, ; 136: System.Globalization.dll => 0x34505120 => 42
	i32 878954865, ; 137: System.Net.Http.Json => 0x3463c971 => 63
	i32 904024072, ; 138: System.ComponentModel.Primitives.dll => 0x35e25008 => 16
	i32 911108515, ; 139: System.IO.MemoryMappedFiles.dll => 0x364e69a3 => 53
	i32 917108320, ; 140: itext.io => 0x36a9f660 => 179
	i32 926902833, ; 141: tr/Microsoft.Maui.Controls.resources.dll => 0x373f6a31 => 340
	i32 928116545, ; 142: Xamarin.Google.Guava.ListenableFuture => 0x3751ef41 => 304
	i32 952186615, ; 143: System.Runtime.InteropServices.JavaScript.dll => 0x38c136f7 => 105
	i32 955402788, ; 144: Newtonsoft.Json => 0x38f24a24 => 224
	i32 956575887, ; 145: Xamarin.Kotlin.StdLib.Jdk8.dll => 0x3904308f => 309
	i32 966729478, ; 146: Xamarin.Google.Crypto.Tink.Android => 0x399f1f06 => 302
	i32 967690846, ; 147: Xamarin.AndroidX.Lifecycle.Common.dll => 0x39adca5e => 265
	i32 971744099, ; 148: itext.barcodes.dll => 0x39eba363 => 176
	i32 975236339, ; 149: System.Diagnostics.Tracing => 0x3a20ecf3 => 34
	i32 975874589, ; 150: System.Xml.XDocument => 0x3a2aaa1d => 158
	i32 986514023, ; 151: System.Private.DataContractSerialization.dll => 0x3acd0267 => 85
	i32 987214855, ; 152: System.Diagnostics.Tools => 0x3ad7b407 => 32
	i32 992768348, ; 153: System.Collections.dll => 0x3b2c715c => 12
	i32 994442037, ; 154: System.IO.FileSystem => 0x3b45fb35 => 51
	i32 999186168, ; 155: Microsoft.Extensions.FileSystemGlobbing.dll => 0x3b8e5ef8 => 208
	i32 1001831731, ; 156: System.IO.UnmanagedMemoryStream.dll => 0x3bb6bd33 => 56
	i32 1012816738, ; 157: Xamarin.AndroidX.SavedState.dll => 0x3c5e5b62 => 285
	i32 1019214401, ; 158: System.Drawing => 0x3cbffa41 => 36
	i32 1028951442, ; 159: Microsoft.Extensions.DependencyInjection.Abstractions => 0x3d548d92 => 202
	i32 1029334545, ; 160: da/Microsoft.Maui.Controls.resources.dll => 0x3d5a6611 => 315
	i32 1031528504, ; 161: Xamarin.Google.ErrorProne.Annotations.dll => 0x3d7be038 => 303
	i32 1035644815, ; 162: Xamarin.AndroidX.AppCompat => 0x3dbaaf8f => 238
	i32 1036536393, ; 163: System.Drawing.Primitives.dll => 0x3dc84a49 => 35
	i32 1044663988, ; 164: System.Linq.Expressions.dll => 0x3e444eb4 => 58
	i32 1052210849, ; 165: Xamarin.AndroidX.Lifecycle.ViewModel.dll => 0x3eb776a1 => 272
	i32 1067306892, ; 166: GoogleGson => 0x3f9dcf8c => 175
	i32 1067609627, ; 167: itext.forms => 0x3fa26e1b => 178
	i32 1074246011, ; 168: itext.kernel.dll => 0x4007b17b => 180
	i32 1082857460, ; 169: System.ComponentModel.TypeConverter => 0x408b17f4 => 17
	i32 1083751839, ; 170: System.IO.Packaging => 0x4098bd9f => 227
	i32 1084122840, ; 171: Xamarin.Kotlin.StdLib => 0x409e66d8 => 306
	i32 1098259244, ; 172: System => 0x41761b2c => 164
	i32 1099692271, ; 173: Microsoft.DotNet.PlatformAbstractions => 0x418bf8ef => 195
	i32 1106973742, ; 174: Microsoft.Extensions.Configuration.FileExtensions.dll => 0x41fb142e => 199
	i32 1118262833, ; 175: ko\Microsoft.Maui.Controls.resources => 0x42a75631 => 328
	i32 1121599056, ; 176: Xamarin.AndroidX.Lifecycle.Runtime.Ktx.dll => 0x42da3e50 => 271
	i32 1127624469, ; 177: Microsoft.Extensions.Logging.Debug => 0x43362f15 => 213
	i32 1149092582, ; 178: Xamarin.AndroidX.Window => 0x447dc2e6 => 298
	i32 1168523401, ; 179: pt\Microsoft.Maui.Controls.resources => 0x45a64089 => 334
	i32 1170634674, ; 180: System.Web.dll => 0x45c677b2 => 153
	i32 1173126369, ; 181: Microsoft.Extensions.FileProviders.Abstractions.dll => 0x45ec7ce1 => 204
	i32 1175144683, ; 182: Xamarin.AndroidX.VectorDrawable.Animated => 0x460b48eb => 294
	i32 1178241025, ; 183: Xamarin.AndroidX.Navigation.Runtime.dll => 0x463a8801 => 279
	i32 1203215381, ; 184: pl/Microsoft.Maui.Controls.resources.dll => 0x47b79c15 => 332
	i32 1204270330, ; 185: Xamarin.AndroidX.Arch.Core.Common => 0x47c7b4fa => 240
	i32 1208641965, ; 186: System.Diagnostics.Process => 0x480a69ad => 29
	i32 1219128291, ; 187: System.IO.IsolatedStorage => 0x48aa6be3 => 52
	i32 1222247595, ; 188: itext.pdfua.dll => 0x48da04ab => 183
	i32 1234928153, ; 189: nb/Microsoft.Maui.Controls.resources.dll => 0x499b8219 => 330
	i32 1243150071, ; 190: Xamarin.AndroidX.Window.Extensions.Core.Core.dll => 0x4a18f6f7 => 299
	i32 1245460359, ; 191: itext.svg => 0x4a3c3787 => 186
	i32 1250430400, ; 192: itext.commons.dll => 0x4a880dc0 => 187
	i32 1253011324, ; 193: Microsoft.Win32.Registry => 0x4aaf6f7c => 5
	i32 1260983243, ; 194: cs\Microsoft.Maui.Controls.resources => 0x4b2913cb => 314
	i32 1264511973, ; 195: Xamarin.AndroidX.Startup.StartupRuntime.dll => 0x4b5eebe5 => 289
	i32 1267360935, ; 196: Xamarin.AndroidX.VectorDrawable => 0x4b8a64a7 => 293
	i32 1273260888, ; 197: Xamarin.AndroidX.Collection.Ktx => 0x4be46b58 => 245
	i32 1275534314, ; 198: Xamarin.KotlinX.Coroutines.Android => 0x4c071bea => 310
	i32 1278448581, ; 199: Xamarin.AndroidX.Annotation.Jvm => 0x4c3393c5 => 237
	i32 1278779541, ; 200: itext.pdfa.dll => 0x4c38a095 => 182
	i32 1293217323, ; 201: Xamarin.AndroidX.DrawerLayout.dll => 0x4d14ee2b => 256
	i32 1309188875, ; 202: System.Private.DataContractSerialization => 0x4e08a30b => 85
	i32 1309401257, ; 203: Xceed.Words.NET => 0x4e0be0a9 => 174
	i32 1322716291, ; 204: Xamarin.AndroidX.Window.dll => 0x4ed70c83 => 298
	i32 1324164729, ; 205: System.Linq => 0x4eed2679 => 61
	i32 1335329327, ; 206: System.Runtime.Serialization.Json.dll => 0x4f97822f => 112
	i32 1364015309, ; 207: System.IO => 0x514d38cd => 57
	i32 1373134921, ; 208: zh-Hans\Microsoft.Maui.Controls.resources => 0x51d86049 => 344
	i32 1376866003, ; 209: Xamarin.AndroidX.SavedState => 0x52114ed3 => 285
	i32 1379779777, ; 210: System.Resources.ResourceManager => 0x523dc4c1 => 99
	i32 1402170036, ; 211: System.Configuration.dll => 0x53936ab4 => 19
	i32 1406073936, ; 212: Xamarin.AndroidX.CoordinatorLayout => 0x53cefc50 => 249
	i32 1408764838, ; 213: System.Runtime.Serialization.Formatters.dll => 0x53f80ba6 => 111
	i32 1411638395, ; 214: System.Runtime.CompilerServices.Unsafe => 0x5423e47b => 101
	i32 1422545099, ; 215: System.Runtime.CompilerServices.VisualC => 0x54ca50cb => 102
	i32 1430672901, ; 216: ar\Microsoft.Maui.Controls.resources => 0x55465605 => 312
	i32 1434145427, ; 217: System.Runtime.Handles => 0x557b5293 => 104
	i32 1435222561, ; 218: Xamarin.Google.Crypto.Tink.Android.dll => 0x558bc221 => 302
	i32 1439761251, ; 219: System.Net.Quic.dll => 0x55d10363 => 71
	i32 1452070440, ; 220: System.Formats.Asn1.dll => 0x568cd628 => 38
	i32 1453312822, ; 221: System.Diagnostics.Tools.dll => 0x569fcb36 => 32
	i32 1454105418, ; 222: Microsoft.Extensions.FileProviders.Composite => 0x56abe34a => 205
	i32 1457743152, ; 223: System.Runtime.Extensions.dll => 0x56e36530 => 103
	i32 1458022317, ; 224: System.Net.Security.dll => 0x56e7a7ad => 73
	i32 1461004990, ; 225: es\Microsoft.Maui.Controls.resources => 0x57152abe => 318
	i32 1461234159, ; 226: System.Collections.Immutable.dll => 0x5718a9ef => 9
	i32 1461719063, ; 227: System.Security.Cryptography.OpenSsl => 0x57201017 => 123
	i32 1462112819, ; 228: System.IO.Compression.dll => 0x57261233 => 46
	i32 1469204771, ; 229: Xamarin.AndroidX.AppCompat.AppCompatResources => 0x57924923 => 239
	i32 1470490898, ; 230: Microsoft.Extensions.Primitives => 0x57a5e912 => 216
	i32 1479771757, ; 231: System.Collections.Immutable => 0x5833866d => 9
	i32 1480492111, ; 232: System.IO.Compression.Brotli.dll => 0x583e844f => 43
	i32 1487239319, ; 233: Microsoft.Win32.Primitives => 0x58a57897 => 4
	i32 1488664300, ; 234: itext.bouncy-castle-connector.dll => 0x58bb36ec => 177
	i32 1490025113, ; 235: Xamarin.AndroidX.SavedState.SavedState.Ktx.dll => 0x58cffa99 => 286
	i32 1493001747, ; 236: hi/Microsoft.Maui.Controls.resources.dll => 0x58fd6613 => 322
	i32 1514721132, ; 237: el/Microsoft.Maui.Controls.resources.dll => 0x5a48cf6c => 317
	i32 1521091094, ; 238: Microsoft.Extensions.FileSystemGlobbing => 0x5aaa0216 => 208
	i32 1536373174, ; 239: System.Diagnostics.TextWriterTraceListener => 0x5b9331b6 => 31
	i32 1543031311, ; 240: System.Text.RegularExpressions.dll => 0x5bf8ca0f => 138
	i32 1543355203, ; 241: System.Reflection.Emit.dll => 0x5bfdbb43 => 92
	i32 1546581739, ; 242: Microsoft.AspNetCore.Components.WebView.Maui.dll => 0x5c2ef6eb => 193
	i32 1550322496, ; 243: System.Reflection.Extensions.dll => 0x5c680b40 => 93
	i32 1551623176, ; 244: sk/Microsoft.Maui.Controls.resources.dll => 0x5c7be408 => 337
	i32 1565862583, ; 245: System.IO.FileSystem.Primitives => 0x5d552ab7 => 49
	i32 1566207040, ; 246: System.Threading.Tasks.Dataflow.dll => 0x5d5a6c40 => 141
	i32 1573704789, ; 247: System.Runtime.Serialization.Json => 0x5dccd455 => 112
	i32 1580037396, ; 248: System.Threading.Overlapped => 0x5e2d7514 => 140
	i32 1582372066, ; 249: Xamarin.AndroidX.DocumentFile.dll => 0x5e5114e2 => 255
	i32 1592978981, ; 250: System.Runtime.Serialization.dll => 0x5ef2ee25 => 115
	i32 1597949149, ; 251: Xamarin.Google.ErrorProne.Annotations => 0x5f3ec4dd => 303
	i32 1601112923, ; 252: System.Xml.Serialization => 0x5f6f0b5b => 157
	i32 1604827217, ; 253: System.Net.WebClient => 0x5fa7b851 => 76
	i32 1618516317, ; 254: System.Net.WebSockets.Client.dll => 0x6078995d => 79
	i32 1622152042, ; 255: Xamarin.AndroidX.Loader.dll => 0x60b0136a => 275
	i32 1622358360, ; 256: System.Dynamic.Runtime => 0x60b33958 => 37
	i32 1624863272, ; 257: Xamarin.AndroidX.ViewPager2 => 0x60d97228 => 297
	i32 1632842087, ; 258: Microsoft.Extensions.Configuration.Json => 0x61533167 => 200
	i32 1635184631, ; 259: Xamarin.AndroidX.Emoji2.ViewsHelper => 0x6176eff7 => 259
	i32 1636350590, ; 260: Xamarin.AndroidX.CursorAdapter => 0x6188ba7e => 252
	i32 1639515021, ; 261: System.Net.Http.dll => 0x61b9038d => 64
	i32 1639986890, ; 262: System.Text.RegularExpressions => 0x61c036ca => 138
	i32 1641389582, ; 263: System.ComponentModel.EventBasedAsync.dll => 0x61d59e0e => 15
	i32 1654881142, ; 264: Microsoft.AspNetCore.Components.WebView => 0x62a37b76 => 192
	i32 1657153582, ; 265: System.Runtime => 0x62c6282e => 116
	i32 1658241508, ; 266: Xamarin.AndroidX.Tracing.Tracing.dll => 0x62d6c1e4 => 291
	i32 1658251792, ; 267: Xamarin.Google.Android.Material.dll => 0x62d6ea10 => 300
	i32 1670060433, ; 268: Xamarin.AndroidX.ConstraintLayout => 0x638b1991 => 247
	i32 1672083785, ; 269: itext.pdfa => 0x63a9f949 => 182
	i32 1675553242, ; 270: System.IO.FileSystem.DriveInfo.dll => 0x63dee9da => 48
	i32 1677501392, ; 271: System.Net.Primitives.dll => 0x63fca3d0 => 70
	i32 1678508291, ; 272: System.Net.WebSockets => 0x640c0103 => 80
	i32 1679769178, ; 273: System.Security.Cryptography => 0x641f3e5a => 126
	i32 1691477237, ; 274: System.Reflection.Metadata => 0x64d1e4f5 => 94
	i32 1696967625, ; 275: System.Security.Cryptography.Csp => 0x6525abc9 => 121
	i32 1698840827, ; 276: Xamarin.Kotlin.StdLib.Common => 0x654240fb => 307
	i32 1701541528, ; 277: System.Diagnostics.Debug.dll => 0x656b7698 => 26
	i32 1720223769, ; 278: Xamarin.AndroidX.Lifecycle.LiveData.Core.Ktx => 0x66888819 => 268
	i32 1726116996, ; 279: System.Reflection.dll => 0x66e27484 => 97
	i32 1728033016, ; 280: System.Diagnostics.FileVersionInfo.dll => 0x66ffb0f8 => 28
	i32 1729485958, ; 281: Xamarin.AndroidX.CardView.dll => 0x6715dc86 => 243
	i32 1736233607, ; 282: ro/Microsoft.Maui.Controls.resources.dll => 0x677cd287 => 335
	i32 1743415430, ; 283: ca\Microsoft.Maui.Controls.resources => 0x67ea6886 => 313
	i32 1744735666, ; 284: System.Transactions.Local.dll => 0x67fe8db2 => 149
	i32 1746115085, ; 285: System.IO.Pipelines.dll => 0x68139a0d => 228
	i32 1746316138, ; 286: Mono.Android.Export => 0x6816ab6a => 169
	i32 1750313021, ; 287: Microsoft.Win32.Primitives.dll => 0x6853a83d => 4
	i32 1758240030, ; 288: System.Resources.Reader.dll => 0x68cc9d1e => 98
	i32 1760259689, ; 289: Microsoft.AspNetCore.Components.Web.dll => 0x68eb6e69 => 191
	i32 1763938596, ; 290: System.Diagnostics.TraceSource.dll => 0x69239124 => 33
	i32 1765942094, ; 291: System.Reflection.Extensions => 0x6942234e => 93
	i32 1766324549, ; 292: Xamarin.AndroidX.SwipeRefreshLayout => 0x6947f945 => 290
	i32 1770582343, ; 293: Microsoft.Extensions.Logging.dll => 0x6988f147 => 209
	i32 1776026572, ; 294: System.Core.dll => 0x69dc03cc => 21
	i32 1777075843, ; 295: System.Globalization.Extensions.dll => 0x69ec0683 => 41
	i32 1780572499, ; 296: Mono.Android.Runtime.dll => 0x6a216153 => 170
	i32 1782862114, ; 297: ms\Microsoft.Maui.Controls.resources => 0x6a445122 => 329
	i32 1788241197, ; 298: Xamarin.AndroidX.Fragment => 0x6a96652d => 261
	i32 1793755602, ; 299: he\Microsoft.Maui.Controls.resources => 0x6aea89d2 => 321
	i32 1808609942, ; 300: Xamarin.AndroidX.Loader => 0x6bcd3296 => 275
	i32 1813058853, ; 301: Xamarin.Kotlin.StdLib.dll => 0x6c111525 => 306
	i32 1813201214, ; 302: Xamarin.Google.Android.Material => 0x6c13413e => 300
	i32 1818569960, ; 303: Xamarin.AndroidX.Navigation.UI.dll => 0x6c652ce8 => 280
	i32 1818787751, ; 304: Microsoft.VisualBasic.Core => 0x6c687fa7 => 2
	i32 1824175904, ; 305: System.Text.Encoding.Extensions => 0x6cbab720 => 134
	i32 1824722060, ; 306: System.Runtime.Serialization.Formatters => 0x6cc30c8c => 111
	i32 1828688058, ; 307: Microsoft.Extensions.Logging.Abstractions.dll => 0x6cff90ba => 210
	i32 1842015223, ; 308: uk/Microsoft.Maui.Controls.resources.dll => 0x6dcaebf7 => 341
	i32 1847515442, ; 309: Xamarin.Android.Glide.Annotations => 0x6e1ed932 => 230
	i32 1853025655, ; 310: sv\Microsoft.Maui.Controls.resources => 0x6e72ed77 => 338
	i32 1858542181, ; 311: System.Linq.Expressions => 0x6ec71a65 => 58
	i32 1870277092, ; 312: System.Reflection.Primitives => 0x6f7a29e4 => 95
	i32 1875935024, ; 313: fr\Microsoft.Maui.Controls.resources => 0x6fd07f30 => 320
	i32 1879696579, ; 314: System.Formats.Tar.dll => 0x7009e4c3 => 39
	i32 1885316902, ; 315: Xamarin.AndroidX.Arch.Core.Runtime.dll => 0x705fa726 => 241
	i32 1888955245, ; 316: System.Diagnostics.Contracts => 0x70972b6d => 25
	i32 1889954781, ; 317: System.Reflection.Metadata.dll => 0x70a66bdd => 94
	i32 1894524299, ; 318: Microsoft.DotNet.PlatformAbstractions.dll => 0x70ec258b => 195
	i32 1898237753, ; 319: System.Reflection.DispatchProxy => 0x7124cf39 => 89
	i32 1900610850, ; 320: System.Resources.ResourceManager.dll => 0x71490522 => 99
	i32 1910275211, ; 321: System.Collections.NonGeneric.dll => 0x71dc7c8b => 10
	i32 1939592360, ; 322: System.Private.Xml.Linq => 0x739bd4a8 => 87
	i32 1956758971, ; 323: System.Resources.Writer => 0x74a1c5bb => 100
	i32 1961813231, ; 324: Xamarin.AndroidX.Security.SecurityCrypto.dll => 0x74eee4ef => 287
	i32 1968388702, ; 325: Microsoft.Extensions.Configuration.dll => 0x75533a5e => 196
	i32 1983156543, ; 326: Xamarin.Kotlin.StdLib.Common.dll => 0x7634913f => 307
	i32 1985761444, ; 327: Xamarin.Android.Glide.GifDecoder => 0x765c50a4 => 232
	i32 2001410897, ; 328: PROGPOEPART2ST10091991 => 0x774b1b51 => 0
	i32 2003115576, ; 329: el\Microsoft.Maui.Controls.resources => 0x77651e38 => 317
	i32 2011961780, ; 330: System.Buffers.dll => 0x77ec19b4 => 7
	i32 2019465201, ; 331: Xamarin.AndroidX.Lifecycle.ViewModel => 0x785e97f1 => 272
	i32 2025202353, ; 332: ar/Microsoft.Maui.Controls.resources.dll => 0x78b622b1 => 312
	i32 2031763787, ; 333: Xamarin.Android.Glide => 0x791a414b => 229
	i32 2045470958, ; 334: System.Private.Xml => 0x79eb68ee => 88
	i32 2045845235, ; 335: itext.pdfua => 0x79f11ef3 => 183
	i32 2047019682, ; 336: PROGPOEPART2ST10091991.dll => 0x7a030aa2 => 0
	i32 2048278909, ; 337: Microsoft.Extensions.Configuration.Binder.dll => 0x7a16417d => 198
	i32 2055257422, ; 338: Xamarin.AndroidX.Lifecycle.LiveData.Core.dll => 0x7a80bd4e => 267
	i32 2060060697, ; 339: System.Windows.dll => 0x7aca0819 => 154
	i32 2066184531, ; 340: de\Microsoft.Maui.Controls.resources => 0x7b277953 => 316
	i32 2070888862, ; 341: System.Diagnostics.TraceSource => 0x7b6f419e => 33
	i32 2072397586, ; 342: Microsoft.Extensions.FileProviders.Physical => 0x7b864712 => 207
	i32 2079903147, ; 343: System.Runtime.dll => 0x7bf8cdab => 116
	i32 2090596640, ; 344: System.Numerics.Vectors => 0x7c9bf920 => 82
	i32 2113721442, ; 345: Xceed.Words.NET.dll => 0x7dfcd462 => 174
	i32 2127167465, ; 346: System.Console => 0x7ec9ffe9 => 20
	i32 2142473426, ; 347: System.Collections.Specialized => 0x7fb38cd2 => 11
	i32 2143790110, ; 348: System.Xml.XmlSerializer.dll => 0x7fc7a41e => 162
	i32 2146852085, ; 349: Microsoft.VisualBasic.dll => 0x7ff65cf5 => 3
	i32 2159891885, ; 350: Microsoft.Maui => 0x80bd55ad => 221
	i32 2169148018, ; 351: hu\Microsoft.Maui.Controls.resources => 0x814a9272 => 324
	i32 2181898931, ; 352: Microsoft.Extensions.Options.dll => 0x820d22b3 => 214
	i32 2192057212, ; 353: Microsoft.Extensions.Logging.Abstractions => 0x82a8237c => 210
	i32 2193016926, ; 354: System.ObjectModel.dll => 0x82b6c85e => 84
	i32 2197979891, ; 355: Microsoft.Extensions.DependencyModel.dll => 0x830282f3 => 203
	i32 2201107256, ; 356: Xamarin.KotlinX.Coroutines.Core.Jvm.dll => 0x83323b38 => 311
	i32 2201231467, ; 357: System.Net.Http => 0x8334206b => 64
	i32 2207618523, ; 358: it\Microsoft.Maui.Controls.resources => 0x839595db => 326
	i32 2217644978, ; 359: Xamarin.AndroidX.VectorDrawable.Animated.dll => 0x842e93b2 => 294
	i32 2222056684, ; 360: System.Threading.Tasks.Parallel => 0x8471e4ec => 143
	i32 2244775296, ; 361: Xamarin.AndroidX.LocalBroadcastManager => 0x85cc8d80 => 276
	i32 2252106437, ; 362: System.Xml.Serialization.dll => 0x863c6ac5 => 157
	i32 2256313426, ; 363: System.Globalization.Extensions => 0x867c9c52 => 41
	i32 2265110946, ; 364: System.Security.AccessControl.dll => 0x8702d9a2 => 117
	i32 2266799131, ; 365: Microsoft.Extensions.Configuration.Abstractions => 0x871c9c1b => 197
	i32 2267999099, ; 366: Xamarin.Android.Glide.DiskLruCache.dll => 0x872eeb7b => 231
	i32 2270573516, ; 367: fr/Microsoft.Maui.Controls.resources.dll => 0x875633cc => 320
	i32 2279755925, ; 368: Xamarin.AndroidX.RecyclerView.dll => 0x87e25095 => 283
	i32 2293034957, ; 369: System.ServiceModel.Web.dll => 0x88acefcd => 131
	i32 2295906218, ; 370: System.Net.Sockets => 0x88d8bfaa => 75
	i32 2298471582, ; 371: System.Net.Mail => 0x88ffe49e => 66
	i32 2303942373, ; 372: nb\Microsoft.Maui.Controls.resources => 0x89535ee5 => 330
	i32 2305521784, ; 373: System.Private.CoreLib.dll => 0x896b7878 => 172
	i32 2315684594, ; 374: Xamarin.AndroidX.Annotation.dll => 0x8a068af2 => 235
	i32 2320631194, ; 375: System.Threading.Tasks.Parallel.dll => 0x8a52059a => 143
	i32 2340441535, ; 376: System.Runtime.InteropServices.RuntimeInformation.dll => 0x8b804dbf => 106
	i32 2344264397, ; 377: System.ValueTuple => 0x8bbaa2cd => 151
	i32 2353062107, ; 378: System.Net.Primitives => 0x8c40e0db => 70
	i32 2368005991, ; 379: System.Xml.ReaderWriter.dll => 0x8d24e767 => 156
	i32 2371007202, ; 380: Microsoft.Extensions.Configuration => 0x8d52b2e2 => 196
	i32 2378619854, ; 381: System.Security.Cryptography.Csp.dll => 0x8dc6dbce => 121
	i32 2383496789, ; 382: System.Security.Principal.Windows.dll => 0x8e114655 => 127
	i32 2395872292, ; 383: id\Microsoft.Maui.Controls.resources => 0x8ece1c24 => 325
	i32 2401565422, ; 384: System.Web.HttpUtility => 0x8f24faee => 152
	i32 2403452196, ; 385: Xamarin.AndroidX.Emoji2.dll => 0x8f41c524 => 258
	i32 2411328690, ; 386: Microsoft.AspNetCore.Components => 0x8fb9f4b2 => 189
	i32 2421380589, ; 387: System.Threading.Tasks.Dataflow => 0x905355ed => 141
	i32 2423080555, ; 388: Xamarin.AndroidX.Collection.Ktx.dll => 0x906d466b => 245
	i32 2427813419, ; 389: hi\Microsoft.Maui.Controls.resources => 0x90b57e2b => 322
	i32 2435356389, ; 390: System.Console.dll => 0x912896e5 => 20
	i32 2435904999, ; 391: System.ComponentModel.DataAnnotations.dll => 0x9130f5e7 => 14
	i32 2442556106, ; 392: Microsoft.JSInterop.dll => 0x919672ca => 217
	i32 2454642406, ; 393: System.Text.Encoding.dll => 0x924edee6 => 135
	i32 2458678730, ; 394: System.Net.Sockets.dll => 0x928c75ca => 75
	i32 2459001652, ; 395: System.Linq.Parallel.dll => 0x92916334 => 59
	i32 2465532216, ; 396: Xamarin.AndroidX.ConstraintLayout.Core.dll => 0x92f50938 => 248
	i32 2471841756, ; 397: netstandard.dll => 0x93554fdc => 167
	i32 2475788418, ; 398: Java.Interop.dll => 0x93918882 => 168
	i32 2480646305, ; 399: Microsoft.Maui.Controls => 0x93dba8a1 => 219
	i32 2483903535, ; 400: System.ComponentModel.EventBasedAsync => 0x940d5c2f => 15
	i32 2484371297, ; 401: System.Net.ServicePoint => 0x94147f61 => 74
	i32 2490993605, ; 402: System.AppContext.dll => 0x94798bc5 => 6
	i32 2501346920, ; 403: System.Data.DataSetExtensions => 0x95178668 => 23
	i32 2505896520, ; 404: Xamarin.AndroidX.Lifecycle.Runtime.dll => 0x955cf248 => 270
	i32 2522472828, ; 405: Xamarin.Android.Glide.dll => 0x9659e17c => 229
	i32 2537015816, ; 406: Microsoft.AspNetCore.Authorization => 0x9737ca08 => 188
	i32 2538310050, ; 407: System.Reflection.Emit.Lightweight.dll => 0x974b89a2 => 91
	i32 2550873716, ; 408: hr\Microsoft.Maui.Controls.resources => 0x980b3e74 => 323
	i32 2562349572, ; 409: Microsoft.CSharp => 0x98ba5a04 => 1
	i32 2566497382, ; 410: itext.bouncy-castle-connector => 0x98f9a466 => 177
	i32 2570120770, ; 411: System.Text.Encodings.Web => 0x9930ee42 => 136
	i32 2573607077, ; 412: itext.kernel => 0x996620a5 => 180
	i32 2581783588, ; 413: Xamarin.AndroidX.Lifecycle.Runtime.Ktx => 0x99e2e424 => 271
	i32 2581819634, ; 414: Xamarin.AndroidX.VectorDrawable.dll => 0x99e370f2 => 293
	i32 2585220780, ; 415: System.Text.Encoding.Extensions.dll => 0x9a1756ac => 134
	i32 2585805581, ; 416: System.Net.Ping => 0x9a20430d => 69
	i32 2585813321, ; 417: Microsoft.AspNetCore.Components.Forms => 0x9a206149 => 190
	i32 2589602615, ; 418: System.Threading.ThreadPool => 0x9a5a3337 => 146
	i32 2592341985, ; 419: Microsoft.Extensions.FileProviders.Abstractions => 0x9a83ffe1 => 204
	i32 2593496499, ; 420: pl\Microsoft.Maui.Controls.resources => 0x9a959db3 => 332
	i32 2605712449, ; 421: Xamarin.KotlinX.Coroutines.Core.Jvm => 0x9b500441 => 311
	i32 2615233544, ; 422: Xamarin.AndroidX.Fragment.Ktx => 0x9be14c08 => 262
	i32 2616218305, ; 423: Microsoft.Extensions.Logging.Debug.dll => 0x9bf052c1 => 213
	i32 2617129537, ; 424: System.Private.Xml.dll => 0x9bfe3a41 => 88
	i32 2618712057, ; 425: System.Reflection.TypeExtensions.dll => 0x9c165ff9 => 96
	i32 2620871830, ; 426: Xamarin.AndroidX.CursorAdapter.dll => 0x9c375496 => 252
	i32 2624644809, ; 427: Xamarin.AndroidX.DynamicAnimation => 0x9c70e6c9 => 257
	i32 2626831493, ; 428: ja\Microsoft.Maui.Controls.resources => 0x9c924485 => 327
	i32 2627185994, ; 429: System.Diagnostics.TextWriterTraceListener.dll => 0x9c97ad4a => 31
	i32 2629843544, ; 430: System.IO.Compression.ZipFile.dll => 0x9cc03a58 => 45
	i32 2633051222, ; 431: Xamarin.AndroidX.Lifecycle.LiveData => 0x9cf12c56 => 266
	i32 2663391936, ; 432: Xamarin.Android.Glide.DiskLruCache => 0x9ec022c0 => 231
	i32 2663698177, ; 433: System.Runtime.Loader => 0x9ec4cf01 => 109
	i32 2664396074, ; 434: System.Xml.XDocument.dll => 0x9ecf752a => 158
	i32 2665622720, ; 435: System.Drawing.Primitives => 0x9ee22cc0 => 35
	i32 2676780864, ; 436: System.Data.Common.dll => 0x9f8c6f40 => 22
	i32 2686887180, ; 437: System.Runtime.Serialization.Xml.dll => 0xa026a50c => 114
	i32 2692077919, ; 438: Microsoft.AspNetCore.Components.WebView.dll => 0xa075d95f => 192
	i32 2693849962, ; 439: System.IO.dll => 0xa090e36a => 57
	i32 2701096212, ; 440: Xamarin.AndroidX.Tracing.Tracing => 0xa0ff7514 => 291
	i32 2715334215, ; 441: System.Threading.Tasks.dll => 0xa1d8b647 => 144
	i32 2717744543, ; 442: System.Security.Claims => 0xa1fd7d9f => 118
	i32 2719963679, ; 443: System.Security.Cryptography.Cng.dll => 0xa21f5a1f => 120
	i32 2724373263, ; 444: System.Runtime.Numerics.dll => 0xa262a30f => 110
	i32 2732626843, ; 445: Xamarin.AndroidX.Activity => 0xa2e0939b => 233
	i32 2735172069, ; 446: System.Threading.Channels => 0xa30769e5 => 139
	i32 2735631878, ; 447: Microsoft.AspNetCore.Authorization.dll => 0xa30e6e06 => 188
	i32 2737747696, ; 448: Xamarin.AndroidX.AppCompat.AppCompatResources.dll => 0xa32eb6f0 => 239
	i32 2740948882, ; 449: System.IO.Pipes.AccessControl => 0xa35f8f92 => 54
	i32 2748088231, ; 450: System.Runtime.InteropServices.JavaScript => 0xa3cc7fa7 => 105
	i32 2752995522, ; 451: pt-BR\Microsoft.Maui.Controls.resources => 0xa41760c2 => 333
	i32 2758225723, ; 452: Microsoft.Maui.Controls.Xaml => 0xa4672f3b => 220
	i32 2764765095, ; 453: Microsoft.Maui.dll => 0xa4caf7a7 => 221
	i32 2765824710, ; 454: System.Text.Encoding.CodePages.dll => 0xa4db22c6 => 133
	i32 2770495804, ; 455: Xamarin.Jetbrains.Annotations.dll => 0xa522693c => 305
	i32 2778768386, ; 456: Xamarin.AndroidX.ViewPager.dll => 0xa5a0a402 => 296
	i32 2779977773, ; 457: Xamarin.AndroidX.ResourceInspection.Annotation.dll => 0xa5b3182d => 284
	i32 2785988530, ; 458: th\Microsoft.Maui.Controls.resources => 0xa60ecfb2 => 339
	i32 2788224221, ; 459: Xamarin.AndroidX.Fragment.Ktx.dll => 0xa630ecdd => 262
	i32 2801831435, ; 460: Microsoft.Maui.Graphics => 0xa7008e0b => 223
	i32 2803228030, ; 461: System.Xml.XPath.XDocument.dll => 0xa715dd7e => 159
	i32 2806116107, ; 462: es/Microsoft.Maui.Controls.resources.dll => 0xa741ef0b => 318
	i32 2810250172, ; 463: Xamarin.AndroidX.CoordinatorLayout.dll => 0xa78103bc => 249
	i32 2819470561, ; 464: System.Xml.dll => 0xa80db4e1 => 163
	i32 2821205001, ; 465: System.ServiceProcess.dll => 0xa8282c09 => 132
	i32 2821294376, ; 466: Xamarin.AndroidX.ResourceInspection.Annotation => 0xa8298928 => 284
	i32 2824502124, ; 467: System.Xml.XmlDocument => 0xa85a7b6c => 161
	i32 2831556043, ; 468: nl/Microsoft.Maui.Controls.resources.dll => 0xa8c61dcb => 331
	i32 2833784645, ; 469: Microsoft.AspNetCore.Metadata => 0xa8e81f45 => 194
	i32 2838993487, ; 470: Xamarin.AndroidX.Lifecycle.ViewModel.Ktx.dll => 0xa9379a4f => 273
	i32 2849599387, ; 471: System.Threading.Overlapped.dll => 0xa9d96f9b => 140
	i32 2853208004, ; 472: Xamarin.AndroidX.ViewPager => 0xaa107fc4 => 296
	i32 2855708567, ; 473: Xamarin.AndroidX.Transition => 0xaa36a797 => 292
	i32 2861098320, ; 474: Mono.Android.Export.dll => 0xaa88e550 => 169
	i32 2861189240, ; 475: Microsoft.Maui.Essentials => 0xaa8a4878 => 222
	i32 2870099610, ; 476: Xamarin.AndroidX.Activity.Ktx.dll => 0xab123e9a => 234
	i32 2875164099, ; 477: Jsr305Binding.dll => 0xab5f85c3 => 301
	i32 2875220617, ; 478: System.Globalization.Calendars.dll => 0xab606289 => 40
	i32 2884993177, ; 479: Xamarin.AndroidX.ExifInterface => 0xabf58099 => 260
	i32 2887636118, ; 480: System.Net.dll => 0xac1dd496 => 81
	i32 2892341533, ; 481: Microsoft.AspNetCore.Components.Web => 0xac65a11d => 191
	i32 2899753641, ; 482: System.IO.UnmanagedMemoryStream => 0xacd6baa9 => 56
	i32 2900621748, ; 483: System.Dynamic.Runtime.dll => 0xace3f9b4 => 37
	i32 2901442782, ; 484: System.Reflection => 0xacf080de => 97
	i32 2905242038, ; 485: mscorlib.dll => 0xad2a79b6 => 166
	i32 2908639175, ; 486: itext.sign => 0xad5e4fc7 => 184
	i32 2909740682, ; 487: System.Private.CoreLib => 0xad6f1e8a => 172
	i32 2911054922, ; 488: Microsoft.Extensions.FileProviders.Physical.dll => 0xad832c4a => 207
	i32 2916838712, ; 489: Xamarin.AndroidX.ViewPager2.dll => 0xaddb6d38 => 297
	i32 2919462931, ; 490: System.Numerics.Vectors.dll => 0xae037813 => 82
	i32 2921128767, ; 491: Xamarin.AndroidX.Annotation.Experimental.dll => 0xae1ce33f => 236
	i32 2936416060, ; 492: System.Resources.Reader => 0xaf06273c => 98
	i32 2940926066, ; 493: System.Diagnostics.StackTrace.dll => 0xaf4af872 => 30
	i32 2942453041, ; 494: System.Xml.XPath.XDocument => 0xaf624531 => 159
	i32 2959614098, ; 495: System.ComponentModel.dll => 0xb0682092 => 18
	i32 2968338931, ; 496: System.Security.Principal.Windows => 0xb0ed41f3 => 127
	i32 2971004615, ; 497: Microsoft.Extensions.Options.ConfigurationExtensions.dll => 0xb115eec7 => 215
	i32 2972252294, ; 498: System.Security.Cryptography.Algorithms.dll => 0xb128f886 => 119
	i32 2978675010, ; 499: Xamarin.AndroidX.DrawerLayout => 0xb18af942 => 256
	i32 2987532451, ; 500: Xamarin.AndroidX.Security.SecurityCrypto => 0xb21220a3 => 287
	i32 2996846495, ; 501: Xamarin.AndroidX.Lifecycle.Process.dll => 0xb2a03f9f => 269
	i32 3016983068, ; 502: Xamarin.AndroidX.Startup.StartupRuntime => 0xb3d3821c => 289
	i32 3023353419, ; 503: WindowsBase.dll => 0xb434b64b => 165
	i32 3024354802, ; 504: Xamarin.AndroidX.Legacy.Support.Core.Utils => 0xb443fdf2 => 264
	i32 3038032645, ; 505: _Microsoft.Android.Resource.Designer.dll => 0xb514b305 => 346
	i32 3056245963, ; 506: Xamarin.AndroidX.SavedState.SavedState.Ktx => 0xb62a9ccb => 286
	i32 3057625584, ; 507: Xamarin.AndroidX.Navigation.Common => 0xb63fa9f0 => 277
	i32 3059408633, ; 508: Mono.Android.Runtime => 0xb65adef9 => 170
	i32 3059793426, ; 509: System.ComponentModel.Primitives => 0xb660be12 => 16
	i32 3075834255, ; 510: System.Threading.Tasks => 0xb755818f => 144
	i32 3077302341, ; 511: hu/Microsoft.Maui.Controls.resources.dll => 0xb76be845 => 324
	i32 3090735792, ; 512: System.Security.Cryptography.X509Certificates.dll => 0xb838e2b0 => 125
	i32 3099732863, ; 513: System.Security.Claims.dll => 0xb8c22b7f => 118
	i32 3103600923, ; 514: System.Formats.Asn1 => 0xb8fd311b => 38
	i32 3109243939, ; 515: Microsoft.Extensions.Logging.Configuration => 0xb9534c23 => 211
	i32 3111772706, ; 516: System.Runtime.Serialization => 0xb979e222 => 115
	i32 3121463068, ; 517: System.IO.FileSystem.AccessControl.dll => 0xba0dbf1c => 47
	i32 3124832203, ; 518: System.Threading.Tasks.Extensions => 0xba4127cb => 142
	i32 3132293585, ; 519: System.Security.AccessControl => 0xbab301d1 => 117
	i32 3146401616, ; 520: itext.styledxmlparser => 0xbb8a4750 => 185
	i32 3147165239, ; 521: System.Diagnostics.Tracing.dll => 0xbb95ee37 => 34
	i32 3148237826, ; 522: GoogleGson.dll => 0xbba64c02 => 175
	i32 3159123045, ; 523: System.Reflection.Primitives.dll => 0xbc4c6465 => 95
	i32 3160747431, ; 524: System.IO.MemoryMappedFiles => 0xbc652da7 => 53
	i32 3178803400, ; 525: Xamarin.AndroidX.Navigation.Fragment.dll => 0xbd78b0c8 => 278
	i32 3192346100, ; 526: System.Security.SecureString => 0xbe4755f4 => 129
	i32 3193515020, ; 527: System.Web => 0xbe592c0c => 153
	i32 3204380047, ; 528: System.Data.dll => 0xbefef58f => 24
	i32 3209718065, ; 529: System.Xml.XmlDocument.dll => 0xbf506931 => 161
	i32 3211777861, ; 530: Xamarin.AndroidX.DocumentFile => 0xbf6fd745 => 255
	i32 3220365878, ; 531: System.Threading => 0xbff2e236 => 148
	i32 3226221578, ; 532: System.Runtime.Handles.dll => 0xc04c3c0a => 104
	i32 3251039220, ; 533: System.Reflection.DispatchProxy.dll => 0xc1c6ebf4 => 89
	i32 3258312781, ; 534: Xamarin.AndroidX.CardView => 0xc235e84d => 243
	i32 3265493905, ; 535: System.Linq.Queryable.dll => 0xc2a37b91 => 60
	i32 3265893370, ; 536: System.Threading.Tasks.Extensions.dll => 0xc2a993fa => 142
	i32 3277815716, ; 537: System.Resources.Writer.dll => 0xc35f7fa4 => 100
	i32 3279906254, ; 538: Microsoft.Win32.Registry.dll => 0xc37f65ce => 5
	i32 3280506390, ; 539: System.ComponentModel.Annotations.dll => 0xc3888e16 => 13
	i32 3290767353, ; 540: System.Security.Cryptography.Encoding => 0xc4251ff9 => 122
	i32 3299363146, ; 541: System.Text.Encoding => 0xc4a8494a => 135
	i32 3303498502, ; 542: System.Diagnostics.FileVersionInfo => 0xc4e76306 => 28
	i32 3305363605, ; 543: fi\Microsoft.Maui.Controls.resources => 0xc503d895 => 319
	i32 3316684772, ; 544: System.Net.Requests.dll => 0xc5b097e4 => 72
	i32 3317135071, ; 545: Xamarin.AndroidX.CustomView.dll => 0xc5b776df => 253
	i32 3317144872, ; 546: System.Data => 0xc5b79d28 => 24
	i32 3340387945, ; 547: SkiaSharp => 0xc71a4669 => 225
	i32 3340431453, ; 548: Xamarin.AndroidX.Arch.Core.Runtime => 0xc71af05d => 241
	i32 3342793838, ; 549: itext.barcodes => 0xc73efc6e => 176
	i32 3345895724, ; 550: Xamarin.AndroidX.ProfileInstaller.ProfileInstaller.dll => 0xc76e512c => 282
	i32 3346324047, ; 551: Xamarin.AndroidX.Navigation.Runtime => 0xc774da4f => 279
	i32 3357674450, ; 552: ru\Microsoft.Maui.Controls.resources => 0xc8220bd2 => 336
	i32 3358260929, ; 553: System.Text.Json => 0xc82afec1 => 137
	i32 3362336904, ; 554: Xamarin.AndroidX.Activity.Ktx => 0xc8693088 => 234
	i32 3362522851, ; 555: Xamarin.AndroidX.Core => 0xc86c06e3 => 250
	i32 3366347497, ; 556: Java.Interop => 0xc8a662e9 => 168
	i32 3374999561, ; 557: Xamarin.AndroidX.RecyclerView => 0xc92a6809 => 283
	i32 3381016424, ; 558: da\Microsoft.Maui.Controls.resources => 0xc9863768 => 315
	i32 3395150330, ; 559: System.Runtime.CompilerServices.Unsafe.dll => 0xca5de1fa => 101
	i32 3403906625, ; 560: System.Security.Cryptography.OpenSsl.dll => 0xcae37e41 => 123
	i32 3405233483, ; 561: Xamarin.AndroidX.CustomView.PoolingContainer => 0xcaf7bd4b => 254
	i32 3406629867, ; 562: Microsoft.Extensions.FileProviders.Composite.dll => 0xcb0d0beb => 205
	i32 3421170118, ; 563: Microsoft.Extensions.Configuration.Binder => 0xcbeae9c6 => 198
	i32 3428513518, ; 564: Microsoft.Extensions.DependencyInjection.dll => 0xcc5af6ee => 201
	i32 3429136800, ; 565: System.Xml => 0xcc6479a0 => 163
	i32 3430777524, ; 566: netstandard => 0xcc7d82b4 => 167
	i32 3441283291, ; 567: Xamarin.AndroidX.DynamicAnimation.dll => 0xcd1dd0db => 257
	i32 3445260447, ; 568: System.Formats.Tar => 0xcd5a809f => 39
	i32 3452344032, ; 569: Microsoft.Maui.Controls.Compatibility.dll => 0xcdc696e0 => 218
	i32 3463511458, ; 570: hr/Microsoft.Maui.Controls.resources.dll => 0xce70fda2 => 323
	i32 3464190856, ; 571: Microsoft.AspNetCore.Components.Forms.dll => 0xce7b5b88 => 190
	i32 3471940407, ; 572: System.ComponentModel.TypeConverter.dll => 0xcef19b37 => 17
	i32 3476120550, ; 573: Mono.Android => 0xcf3163e6 => 171
	i32 3479583265, ; 574: ru/Microsoft.Maui.Controls.resources.dll => 0xcf663a21 => 336
	i32 3484440000, ; 575: ro\Microsoft.Maui.Controls.resources => 0xcfb055c0 => 335
	i32 3485117614, ; 576: System.Text.Json.dll => 0xcfbaacae => 137
	i32 3486566296, ; 577: System.Transactions => 0xcfd0c798 => 150
	i32 3493954962, ; 578: Xamarin.AndroidX.Concurrent.Futures.dll => 0xd0418592 => 246
	i32 3500000672, ; 579: Microsoft.JSInterop => 0xd09dc5a0 => 217
	i32 3509114376, ; 580: System.Xml.Linq => 0xd128d608 => 155
	i32 3515174580, ; 581: System.Security.dll => 0xd1854eb4 => 130
	i32 3530912306, ; 582: System.Configuration => 0xd2757232 => 19
	i32 3539954161, ; 583: System.Net.HttpListener => 0xd2ff69f1 => 65
	i32 3560100363, ; 584: System.Threading.Timer => 0xd432d20b => 147
	i32 3570554715, ; 585: System.IO.FileSystem.AccessControl => 0xd4d2575b => 47
	i32 3580758918, ; 586: zh-HK\Microsoft.Maui.Controls.resources => 0xd56e0b86 => 343
	i32 3597029428, ; 587: Xamarin.Android.Glide.GifDecoder.dll => 0xd6665034 => 232
	i32 3598340787, ; 588: System.Net.WebSockets.Client => 0xd67a52b3 => 79
	i32 3602646031, ; 589: Xceed.Document.NET.dll => 0xd6bc040f => 173
	i32 3608519521, ; 590: System.Linq.dll => 0xd715a361 => 61
	i32 3624195450, ; 591: System.Runtime.InteropServices.RuntimeInformation => 0xd804d57a => 106
	i32 3627220390, ; 592: Xamarin.AndroidX.Print.dll => 0xd832fda6 => 281
	i32 3633644679, ; 593: Xamarin.AndroidX.Annotation.Experimental => 0xd8950487 => 236
	i32 3637786959, ; 594: itext.sign.dll => 0xd8d4394f => 184
	i32 3638274909, ; 595: System.IO.FileSystem.Primitives.dll => 0xd8dbab5d => 49
	i32 3641597786, ; 596: Xamarin.AndroidX.Lifecycle.LiveData.Core => 0xd90e5f5a => 267
	i32 3643446276, ; 597: tr\Microsoft.Maui.Controls.resources => 0xd92a9404 => 340
	i32 3643854240, ; 598: Xamarin.AndroidX.Navigation.Fragment => 0xd930cda0 => 278
	i32 3645089577, ; 599: System.ComponentModel.DataAnnotations => 0xd943a729 => 14
	i32 3657292374, ; 600: Microsoft.Extensions.Configuration.Abstractions.dll => 0xd9fdda56 => 197
	i32 3660523487, ; 601: System.Net.NetworkInformation => 0xda2f27df => 68
	i32 3672681054, ; 602: Mono.Android.dll => 0xdae8aa5e => 171
	i32 3682565725, ; 603: Xamarin.AndroidX.Browser => 0xdb7f7e5d => 242
	i32 3684561358, ; 604: Xamarin.AndroidX.Concurrent.Futures => 0xdb9df1ce => 246
	i32 3684657769, ; 605: itext.commons => 0xdb9f6a69 => 187
	i32 3689375977, ; 606: System.Drawing.Common => 0xdbe768e9 => 226
	i32 3697841164, ; 607: zh-Hant/Microsoft.Maui.Controls.resources.dll => 0xdc68940c => 345
	i32 3700866549, ; 608: System.Net.WebProxy.dll => 0xdc96bdf5 => 78
	i32 3706696989, ; 609: Xamarin.AndroidX.Core.Core.Ktx.dll => 0xdcefb51d => 251
	i32 3716563718, ; 610: System.Runtime.Intrinsics => 0xdd864306 => 108
	i32 3718780102, ; 611: Xamarin.AndroidX.Annotation => 0xdda814c6 => 235
	i32 3722202641, ; 612: Microsoft.Extensions.Configuration.Json.dll => 0xdddc4e11 => 200
	i32 3724971120, ; 613: Xamarin.AndroidX.Navigation.Common.dll => 0xde068c70 => 277
	i32 3732100267, ; 614: System.Net.NameResolution => 0xde7354ab => 67
	i32 3732214720, ; 615: Microsoft.AspNetCore.Metadata.dll => 0xde7513c0 => 194
	i32 3737834244, ; 616: System.Net.Http.Json.dll => 0xdecad304 => 63
	i32 3748608112, ; 617: System.Diagnostics.DiagnosticSource => 0xdf6f3870 => 27
	i32 3751444290, ; 618: System.Xml.XPath => 0xdf9a7f42 => 160
	i32 3758424670, ; 619: Microsoft.Extensions.Configuration.FileExtensions => 0xe005025e => 199
	i32 3786282454, ; 620: Xamarin.AndroidX.Collection => 0xe1ae15d6 => 244
	i32 3792276235, ; 621: System.Collections.NonGeneric => 0xe2098b0b => 10
	i32 3800979733, ; 622: Microsoft.Maui.Controls.Compatibility => 0xe28e5915 => 218
	i32 3802395368, ; 623: System.Collections.Specialized.dll => 0xe2a3f2e8 => 11
	i32 3819260425, ; 624: System.Net.WebProxy => 0xe3a54a09 => 78
	i32 3823082795, ; 625: System.Security.Cryptography.dll => 0xe3df9d2b => 126
	i32 3829621856, ; 626: System.Numerics.dll => 0xe4436460 => 83
	i32 3841636137, ; 627: Microsoft.Extensions.DependencyInjection.Abstractions.dll => 0xe4fab729 => 202
	i32 3844307129, ; 628: System.Net.Mail.dll => 0xe52378b9 => 66
	i32 3849253459, ; 629: System.Runtime.InteropServices.dll => 0xe56ef253 => 107
	i32 3870376305, ; 630: System.Net.HttpListener.dll => 0xe6b14171 => 65
	i32 3873536506, ; 631: System.Security.Principal => 0xe6e179fa => 128
	i32 3875112723, ; 632: System.Security.Cryptography.Encoding.dll => 0xe6f98713 => 122
	i32 3885497537, ; 633: System.Net.WebHeaderCollection.dll => 0xe797fcc1 => 77
	i32 3885922214, ; 634: Xamarin.AndroidX.Transition.dll => 0xe79e77a6 => 292
	i32 3888767677, ; 635: Xamarin.AndroidX.ProfileInstaller.ProfileInstaller => 0xe7c9e2bd => 282
	i32 3889960447, ; 636: zh-Hans/Microsoft.Maui.Controls.resources.dll => 0xe7dc15ff => 344
	i32 3896106733, ; 637: System.Collections.Concurrent.dll => 0xe839deed => 8
	i32 3896760992, ; 638: Xamarin.AndroidX.Core.dll => 0xe843daa0 => 250
	i32 3898862222, ; 639: itext.layout.dll => 0xe863ea8e => 181
	i32 3901907137, ; 640: Microsoft.VisualBasic.Core.dll => 0xe89260c1 => 2
	i32 3920810846, ; 641: System.IO.Compression.FileSystem.dll => 0xe9b2d35e => 44
	i32 3921031405, ; 642: Xamarin.AndroidX.VersionedParcelable.dll => 0xe9b630ed => 295
	i32 3928044579, ; 643: System.Xml.ReaderWriter => 0xea213423 => 156
	i32 3930554604, ; 644: System.Security.Principal.dll => 0xea4780ec => 128
	i32 3931092270, ; 645: Xamarin.AndroidX.Navigation.UI => 0xea4fb52e => 280
	i32 3945713374, ; 646: System.Data.DataSetExtensions.dll => 0xeb2ecede => 23
	i32 3952357212, ; 647: System.IO.Packaging.dll => 0xeb942f5c => 227
	i32 3953953790, ; 648: System.Text.Encoding.CodePages => 0xebac8bfe => 133
	i32 3955647286, ; 649: Xamarin.AndroidX.AppCompat.dll => 0xebc66336 => 238
	i32 3959773229, ; 650: Xamarin.AndroidX.Lifecycle.Process => 0xec05582d => 269
	i32 3971066695, ; 651: itext.styledxmlparser.dll => 0xecb1ab47 => 185
	i32 3977646153, ; 652: itext.io.dll => 0xed161049 => 179
	i32 3980434154, ; 653: th/Microsoft.Maui.Controls.resources.dll => 0xed409aea => 339
	i32 3987592930, ; 654: he/Microsoft.Maui.Controls.resources.dll => 0xedadd6e2 => 321
	i32 4003436829, ; 655: System.Diagnostics.Process.dll => 0xee9f991d => 29
	i32 4015948917, ; 656: Xamarin.AndroidX.Annotation.Jvm.dll => 0xef5e8475 => 237
	i32 4023392905, ; 657: System.IO.Pipelines => 0xefd01a89 => 228
	i32 4025784931, ; 658: System.Memory => 0xeff49a63 => 62
	i32 4046471985, ; 659: Microsoft.Maui.Controls.Xaml.dll => 0xf1304331 => 220
	i32 4054681211, ; 660: System.Reflection.Emit.ILGeneration => 0xf1ad867b => 90
	i32 4068434129, ; 661: System.Private.Xml.Linq.dll => 0xf27f60d1 => 87
	i32 4073602200, ; 662: System.Threading.dll => 0xf2ce3c98 => 148
	i32 4075152723, ; 663: Microsoft.Extensions.Logging.Console => 0xf2e5e553 => 212
	i32 4091293555, ; 664: itext.forms.dll => 0xf3dc2f73 => 178
	i32 4094352644, ; 665: Microsoft.Maui.Essentials.dll => 0xf40add04 => 222
	i32 4099507663, ; 666: System.Drawing.dll => 0xf45985cf => 36
	i32 4100113165, ; 667: System.Private.Uri => 0xf462c30d => 86
	i32 4101593132, ; 668: Xamarin.AndroidX.Emoji2 => 0xf479582c => 258
	i32 4102112229, ; 669: pt/Microsoft.Maui.Controls.resources.dll => 0xf48143e5 => 334
	i32 4125707920, ; 670: ms/Microsoft.Maui.Controls.resources.dll => 0xf5e94e90 => 329
	i32 4126470640, ; 671: Microsoft.Extensions.DependencyInjection => 0xf5f4f1f0 => 201
	i32 4127667938, ; 672: System.IO.FileSystem.Watcher => 0xf60736e2 => 50
	i32 4130442656, ; 673: System.AppContext => 0xf6318da0 => 6
	i32 4147896353, ; 674: System.Reflection.Emit.ILGeneration.dll => 0xf73be021 => 90
	i32 4150914736, ; 675: uk\Microsoft.Maui.Controls.resources => 0xf769eeb0 => 341
	i32 4151237749, ; 676: System.Core => 0xf76edc75 => 21
	i32 4159265925, ; 677: System.Xml.XmlSerializer => 0xf7e95c85 => 162
	i32 4161255271, ; 678: System.Reflection.TypeExtensions => 0xf807b767 => 96
	i32 4164802419, ; 679: System.IO.FileSystem.Watcher.dll => 0xf83dd773 => 50
	i32 4181436372, ; 680: System.Runtime.Serialization.Primitives => 0xf93ba7d4 => 113
	i32 4182413190, ; 681: Xamarin.AndroidX.Lifecycle.ViewModelSavedState.dll => 0xf94a8f86 => 274
	i32 4185676441, ; 682: System.Security => 0xf97c5a99 => 130
	i32 4186523351, ; 683: itext.svg.dll => 0xf98946d7 => 186
	i32 4196529839, ; 684: System.Net.WebClient.dll => 0xfa21f6af => 76
	i32 4213026141, ; 685: System.Diagnostics.DiagnosticSource.dll => 0xfb1dad5d => 27
	i32 4256097574, ; 686: Xamarin.AndroidX.Core.Core.Ktx => 0xfdaee526 => 251
	i32 4258378803, ; 687: Xamarin.AndroidX.Lifecycle.ViewModel.Ktx => 0xfdd1b433 => 273
	i32 4260525087, ; 688: System.Buffers => 0xfdf2741f => 7
	i32 4271975918, ; 689: Microsoft.Maui.Controls.dll => 0xfea12dee => 219
	i32 4274976490, ; 690: System.Runtime.Numerics => 0xfecef6ea => 110
	i32 4292120959, ; 691: Xamarin.AndroidX.Lifecycle.ViewModelSavedState => 0xffd4917f => 274
	i32 4294648842, ; 692: Microsoft.Extensions.FileProviders.Embedded => 0xfffb240a => 206
	i32 4294763496 ; 693: Xamarin.AndroidX.ExifInterface.dll => 0xfffce3e8 => 260
], align 4

@assembly_image_cache_indices = dso_local local_unnamed_addr constant [694 x i32] [
	i32 68, ; 0
	i32 67, ; 1
	i32 108, ; 2
	i32 203, ; 3
	i32 270, ; 4
	i32 304, ; 5
	i32 48, ; 6
	i32 224, ; 7
	i32 80, ; 8
	i32 145, ; 9
	i32 30, ; 10
	i32 345, ; 11
	i32 124, ; 12
	i32 223, ; 13
	i32 102, ; 14
	i32 288, ; 15
	i32 107, ; 16
	i32 288, ; 17
	i32 139, ; 18
	i32 308, ; 19
	i32 77, ; 20
	i32 124, ; 21
	i32 13, ; 22
	i32 244, ; 23
	i32 132, ; 24
	i32 290, ; 25
	i32 151, ; 26
	i32 342, ; 27
	i32 343, ; 28
	i32 18, ; 29
	i32 242, ; 30
	i32 26, ; 31
	i32 264, ; 32
	i32 1, ; 33
	i32 59, ; 34
	i32 42, ; 35
	i32 91, ; 36
	i32 189, ; 37
	i32 247, ; 38
	i32 173, ; 39
	i32 147, ; 40
	i32 266, ; 41
	i32 263, ; 42
	i32 314, ; 43
	i32 54, ; 44
	i32 69, ; 45
	i32 342, ; 46
	i32 233, ; 47
	i32 83, ; 48
	i32 327, ; 49
	i32 265, ; 50
	i32 326, ; 51
	i32 131, ; 52
	i32 55, ; 53
	i32 149, ; 54
	i32 74, ; 55
	i32 145, ; 56
	i32 62, ; 57
	i32 146, ; 58
	i32 346, ; 59
	i32 165, ; 60
	i32 338, ; 61
	i32 248, ; 62
	i32 12, ; 63
	i32 261, ; 64
	i32 125, ; 65
	i32 152, ; 66
	i32 113, ; 67
	i32 166, ; 68
	i32 164, ; 69
	i32 263, ; 70
	i32 276, ; 71
	i32 84, ; 72
	i32 325, ; 73
	i32 319, ; 74
	i32 216, ; 75
	i32 225, ; 76
	i32 150, ; 77
	i32 308, ; 78
	i32 60, ; 79
	i32 209, ; 80
	i32 51, ; 81
	i32 103, ; 82
	i32 211, ; 83
	i32 114, ; 84
	i32 40, ; 85
	i32 301, ; 86
	i32 299, ; 87
	i32 206, ; 88
	i32 120, ; 89
	i32 333, ; 90
	i32 52, ; 91
	i32 44, ; 92
	i32 181, ; 93
	i32 119, ; 94
	i32 253, ; 95
	i32 331, ; 96
	i32 259, ; 97
	i32 81, ; 98
	i32 136, ; 99
	i32 295, ; 100
	i32 240, ; 101
	i32 8, ; 102
	i32 73, ; 103
	i32 313, ; 104
	i32 155, ; 105
	i32 310, ; 106
	i32 212, ; 107
	i32 154, ; 108
	i32 92, ; 109
	i32 305, ; 110
	i32 45, ; 111
	i32 328, ; 112
	i32 316, ; 113
	i32 309, ; 114
	i32 109, ; 115
	i32 215, ; 116
	i32 129, ; 117
	i32 25, ; 118
	i32 230, ; 119
	i32 72, ; 120
	i32 55, ; 121
	i32 46, ; 122
	i32 337, ; 123
	i32 214, ; 124
	i32 254, ; 125
	i32 193, ; 126
	i32 22, ; 127
	i32 268, ; 128
	i32 226, ; 129
	i32 86, ; 130
	i32 43, ; 131
	i32 160, ; 132
	i32 71, ; 133
	i32 281, ; 134
	i32 3, ; 135
	i32 42, ; 136
	i32 63, ; 137
	i32 16, ; 138
	i32 53, ; 139
	i32 179, ; 140
	i32 340, ; 141
	i32 304, ; 142
	i32 105, ; 143
	i32 224, ; 144
	i32 309, ; 145
	i32 302, ; 146
	i32 265, ; 147
	i32 176, ; 148
	i32 34, ; 149
	i32 158, ; 150
	i32 85, ; 151
	i32 32, ; 152
	i32 12, ; 153
	i32 51, ; 154
	i32 208, ; 155
	i32 56, ; 156
	i32 285, ; 157
	i32 36, ; 158
	i32 202, ; 159
	i32 315, ; 160
	i32 303, ; 161
	i32 238, ; 162
	i32 35, ; 163
	i32 58, ; 164
	i32 272, ; 165
	i32 175, ; 166
	i32 178, ; 167
	i32 180, ; 168
	i32 17, ; 169
	i32 227, ; 170
	i32 306, ; 171
	i32 164, ; 172
	i32 195, ; 173
	i32 199, ; 174
	i32 328, ; 175
	i32 271, ; 176
	i32 213, ; 177
	i32 298, ; 178
	i32 334, ; 179
	i32 153, ; 180
	i32 204, ; 181
	i32 294, ; 182
	i32 279, ; 183
	i32 332, ; 184
	i32 240, ; 185
	i32 29, ; 186
	i32 52, ; 187
	i32 183, ; 188
	i32 330, ; 189
	i32 299, ; 190
	i32 186, ; 191
	i32 187, ; 192
	i32 5, ; 193
	i32 314, ; 194
	i32 289, ; 195
	i32 293, ; 196
	i32 245, ; 197
	i32 310, ; 198
	i32 237, ; 199
	i32 182, ; 200
	i32 256, ; 201
	i32 85, ; 202
	i32 174, ; 203
	i32 298, ; 204
	i32 61, ; 205
	i32 112, ; 206
	i32 57, ; 207
	i32 344, ; 208
	i32 285, ; 209
	i32 99, ; 210
	i32 19, ; 211
	i32 249, ; 212
	i32 111, ; 213
	i32 101, ; 214
	i32 102, ; 215
	i32 312, ; 216
	i32 104, ; 217
	i32 302, ; 218
	i32 71, ; 219
	i32 38, ; 220
	i32 32, ; 221
	i32 205, ; 222
	i32 103, ; 223
	i32 73, ; 224
	i32 318, ; 225
	i32 9, ; 226
	i32 123, ; 227
	i32 46, ; 228
	i32 239, ; 229
	i32 216, ; 230
	i32 9, ; 231
	i32 43, ; 232
	i32 4, ; 233
	i32 177, ; 234
	i32 286, ; 235
	i32 322, ; 236
	i32 317, ; 237
	i32 208, ; 238
	i32 31, ; 239
	i32 138, ; 240
	i32 92, ; 241
	i32 193, ; 242
	i32 93, ; 243
	i32 337, ; 244
	i32 49, ; 245
	i32 141, ; 246
	i32 112, ; 247
	i32 140, ; 248
	i32 255, ; 249
	i32 115, ; 250
	i32 303, ; 251
	i32 157, ; 252
	i32 76, ; 253
	i32 79, ; 254
	i32 275, ; 255
	i32 37, ; 256
	i32 297, ; 257
	i32 200, ; 258
	i32 259, ; 259
	i32 252, ; 260
	i32 64, ; 261
	i32 138, ; 262
	i32 15, ; 263
	i32 192, ; 264
	i32 116, ; 265
	i32 291, ; 266
	i32 300, ; 267
	i32 247, ; 268
	i32 182, ; 269
	i32 48, ; 270
	i32 70, ; 271
	i32 80, ; 272
	i32 126, ; 273
	i32 94, ; 274
	i32 121, ; 275
	i32 307, ; 276
	i32 26, ; 277
	i32 268, ; 278
	i32 97, ; 279
	i32 28, ; 280
	i32 243, ; 281
	i32 335, ; 282
	i32 313, ; 283
	i32 149, ; 284
	i32 228, ; 285
	i32 169, ; 286
	i32 4, ; 287
	i32 98, ; 288
	i32 191, ; 289
	i32 33, ; 290
	i32 93, ; 291
	i32 290, ; 292
	i32 209, ; 293
	i32 21, ; 294
	i32 41, ; 295
	i32 170, ; 296
	i32 329, ; 297
	i32 261, ; 298
	i32 321, ; 299
	i32 275, ; 300
	i32 306, ; 301
	i32 300, ; 302
	i32 280, ; 303
	i32 2, ; 304
	i32 134, ; 305
	i32 111, ; 306
	i32 210, ; 307
	i32 341, ; 308
	i32 230, ; 309
	i32 338, ; 310
	i32 58, ; 311
	i32 95, ; 312
	i32 320, ; 313
	i32 39, ; 314
	i32 241, ; 315
	i32 25, ; 316
	i32 94, ; 317
	i32 195, ; 318
	i32 89, ; 319
	i32 99, ; 320
	i32 10, ; 321
	i32 87, ; 322
	i32 100, ; 323
	i32 287, ; 324
	i32 196, ; 325
	i32 307, ; 326
	i32 232, ; 327
	i32 0, ; 328
	i32 317, ; 329
	i32 7, ; 330
	i32 272, ; 331
	i32 312, ; 332
	i32 229, ; 333
	i32 88, ; 334
	i32 183, ; 335
	i32 0, ; 336
	i32 198, ; 337
	i32 267, ; 338
	i32 154, ; 339
	i32 316, ; 340
	i32 33, ; 341
	i32 207, ; 342
	i32 116, ; 343
	i32 82, ; 344
	i32 174, ; 345
	i32 20, ; 346
	i32 11, ; 347
	i32 162, ; 348
	i32 3, ; 349
	i32 221, ; 350
	i32 324, ; 351
	i32 214, ; 352
	i32 210, ; 353
	i32 84, ; 354
	i32 203, ; 355
	i32 311, ; 356
	i32 64, ; 357
	i32 326, ; 358
	i32 294, ; 359
	i32 143, ; 360
	i32 276, ; 361
	i32 157, ; 362
	i32 41, ; 363
	i32 117, ; 364
	i32 197, ; 365
	i32 231, ; 366
	i32 320, ; 367
	i32 283, ; 368
	i32 131, ; 369
	i32 75, ; 370
	i32 66, ; 371
	i32 330, ; 372
	i32 172, ; 373
	i32 235, ; 374
	i32 143, ; 375
	i32 106, ; 376
	i32 151, ; 377
	i32 70, ; 378
	i32 156, ; 379
	i32 196, ; 380
	i32 121, ; 381
	i32 127, ; 382
	i32 325, ; 383
	i32 152, ; 384
	i32 258, ; 385
	i32 189, ; 386
	i32 141, ; 387
	i32 245, ; 388
	i32 322, ; 389
	i32 20, ; 390
	i32 14, ; 391
	i32 217, ; 392
	i32 135, ; 393
	i32 75, ; 394
	i32 59, ; 395
	i32 248, ; 396
	i32 167, ; 397
	i32 168, ; 398
	i32 219, ; 399
	i32 15, ; 400
	i32 74, ; 401
	i32 6, ; 402
	i32 23, ; 403
	i32 270, ; 404
	i32 229, ; 405
	i32 188, ; 406
	i32 91, ; 407
	i32 323, ; 408
	i32 1, ; 409
	i32 177, ; 410
	i32 136, ; 411
	i32 180, ; 412
	i32 271, ; 413
	i32 293, ; 414
	i32 134, ; 415
	i32 69, ; 416
	i32 190, ; 417
	i32 146, ; 418
	i32 204, ; 419
	i32 332, ; 420
	i32 311, ; 421
	i32 262, ; 422
	i32 213, ; 423
	i32 88, ; 424
	i32 96, ; 425
	i32 252, ; 426
	i32 257, ; 427
	i32 327, ; 428
	i32 31, ; 429
	i32 45, ; 430
	i32 266, ; 431
	i32 231, ; 432
	i32 109, ; 433
	i32 158, ; 434
	i32 35, ; 435
	i32 22, ; 436
	i32 114, ; 437
	i32 192, ; 438
	i32 57, ; 439
	i32 291, ; 440
	i32 144, ; 441
	i32 118, ; 442
	i32 120, ; 443
	i32 110, ; 444
	i32 233, ; 445
	i32 139, ; 446
	i32 188, ; 447
	i32 239, ; 448
	i32 54, ; 449
	i32 105, ; 450
	i32 333, ; 451
	i32 220, ; 452
	i32 221, ; 453
	i32 133, ; 454
	i32 305, ; 455
	i32 296, ; 456
	i32 284, ; 457
	i32 339, ; 458
	i32 262, ; 459
	i32 223, ; 460
	i32 159, ; 461
	i32 318, ; 462
	i32 249, ; 463
	i32 163, ; 464
	i32 132, ; 465
	i32 284, ; 466
	i32 161, ; 467
	i32 331, ; 468
	i32 194, ; 469
	i32 273, ; 470
	i32 140, ; 471
	i32 296, ; 472
	i32 292, ; 473
	i32 169, ; 474
	i32 222, ; 475
	i32 234, ; 476
	i32 301, ; 477
	i32 40, ; 478
	i32 260, ; 479
	i32 81, ; 480
	i32 191, ; 481
	i32 56, ; 482
	i32 37, ; 483
	i32 97, ; 484
	i32 166, ; 485
	i32 184, ; 486
	i32 172, ; 487
	i32 207, ; 488
	i32 297, ; 489
	i32 82, ; 490
	i32 236, ; 491
	i32 98, ; 492
	i32 30, ; 493
	i32 159, ; 494
	i32 18, ; 495
	i32 127, ; 496
	i32 215, ; 497
	i32 119, ; 498
	i32 256, ; 499
	i32 287, ; 500
	i32 269, ; 501
	i32 289, ; 502
	i32 165, ; 503
	i32 264, ; 504
	i32 346, ; 505
	i32 286, ; 506
	i32 277, ; 507
	i32 170, ; 508
	i32 16, ; 509
	i32 144, ; 510
	i32 324, ; 511
	i32 125, ; 512
	i32 118, ; 513
	i32 38, ; 514
	i32 211, ; 515
	i32 115, ; 516
	i32 47, ; 517
	i32 142, ; 518
	i32 117, ; 519
	i32 185, ; 520
	i32 34, ; 521
	i32 175, ; 522
	i32 95, ; 523
	i32 53, ; 524
	i32 278, ; 525
	i32 129, ; 526
	i32 153, ; 527
	i32 24, ; 528
	i32 161, ; 529
	i32 255, ; 530
	i32 148, ; 531
	i32 104, ; 532
	i32 89, ; 533
	i32 243, ; 534
	i32 60, ; 535
	i32 142, ; 536
	i32 100, ; 537
	i32 5, ; 538
	i32 13, ; 539
	i32 122, ; 540
	i32 135, ; 541
	i32 28, ; 542
	i32 319, ; 543
	i32 72, ; 544
	i32 253, ; 545
	i32 24, ; 546
	i32 225, ; 547
	i32 241, ; 548
	i32 176, ; 549
	i32 282, ; 550
	i32 279, ; 551
	i32 336, ; 552
	i32 137, ; 553
	i32 234, ; 554
	i32 250, ; 555
	i32 168, ; 556
	i32 283, ; 557
	i32 315, ; 558
	i32 101, ; 559
	i32 123, ; 560
	i32 254, ; 561
	i32 205, ; 562
	i32 198, ; 563
	i32 201, ; 564
	i32 163, ; 565
	i32 167, ; 566
	i32 257, ; 567
	i32 39, ; 568
	i32 218, ; 569
	i32 323, ; 570
	i32 190, ; 571
	i32 17, ; 572
	i32 171, ; 573
	i32 336, ; 574
	i32 335, ; 575
	i32 137, ; 576
	i32 150, ; 577
	i32 246, ; 578
	i32 217, ; 579
	i32 155, ; 580
	i32 130, ; 581
	i32 19, ; 582
	i32 65, ; 583
	i32 147, ; 584
	i32 47, ; 585
	i32 343, ; 586
	i32 232, ; 587
	i32 79, ; 588
	i32 173, ; 589
	i32 61, ; 590
	i32 106, ; 591
	i32 281, ; 592
	i32 236, ; 593
	i32 184, ; 594
	i32 49, ; 595
	i32 267, ; 596
	i32 340, ; 597
	i32 278, ; 598
	i32 14, ; 599
	i32 197, ; 600
	i32 68, ; 601
	i32 171, ; 602
	i32 242, ; 603
	i32 246, ; 604
	i32 187, ; 605
	i32 226, ; 606
	i32 345, ; 607
	i32 78, ; 608
	i32 251, ; 609
	i32 108, ; 610
	i32 235, ; 611
	i32 200, ; 612
	i32 277, ; 613
	i32 67, ; 614
	i32 194, ; 615
	i32 63, ; 616
	i32 27, ; 617
	i32 160, ; 618
	i32 199, ; 619
	i32 244, ; 620
	i32 10, ; 621
	i32 218, ; 622
	i32 11, ; 623
	i32 78, ; 624
	i32 126, ; 625
	i32 83, ; 626
	i32 202, ; 627
	i32 66, ; 628
	i32 107, ; 629
	i32 65, ; 630
	i32 128, ; 631
	i32 122, ; 632
	i32 77, ; 633
	i32 292, ; 634
	i32 282, ; 635
	i32 344, ; 636
	i32 8, ; 637
	i32 250, ; 638
	i32 181, ; 639
	i32 2, ; 640
	i32 44, ; 641
	i32 295, ; 642
	i32 156, ; 643
	i32 128, ; 644
	i32 280, ; 645
	i32 23, ; 646
	i32 227, ; 647
	i32 133, ; 648
	i32 238, ; 649
	i32 269, ; 650
	i32 185, ; 651
	i32 179, ; 652
	i32 339, ; 653
	i32 321, ; 654
	i32 29, ; 655
	i32 237, ; 656
	i32 228, ; 657
	i32 62, ; 658
	i32 220, ; 659
	i32 90, ; 660
	i32 87, ; 661
	i32 148, ; 662
	i32 212, ; 663
	i32 178, ; 664
	i32 222, ; 665
	i32 36, ; 666
	i32 86, ; 667
	i32 258, ; 668
	i32 334, ; 669
	i32 329, ; 670
	i32 201, ; 671
	i32 50, ; 672
	i32 6, ; 673
	i32 90, ; 674
	i32 341, ; 675
	i32 21, ; 676
	i32 162, ; 677
	i32 96, ; 678
	i32 50, ; 679
	i32 113, ; 680
	i32 274, ; 681
	i32 130, ; 682
	i32 186, ; 683
	i32 76, ; 684
	i32 27, ; 685
	i32 251, ; 686
	i32 273, ; 687
	i32 7, ; 688
	i32 219, ; 689
	i32 110, ; 690
	i32 274, ; 691
	i32 206, ; 692
	i32 260 ; 693
], align 4

@marshal_methods_number_of_classes = dso_local local_unnamed_addr constant i32 0, align 4

@marshal_methods_class_cache = dso_local local_unnamed_addr global [0 x %struct.MarshalMethodsManagedClass] zeroinitializer, align 4

; Names of classes in which marshal methods reside
@mm_class_names = dso_local local_unnamed_addr constant [0 x ptr] zeroinitializer, align 4

@mm_method_names = dso_local local_unnamed_addr constant [1 x %struct.MarshalMethodName] [
	%struct.MarshalMethodName {
		i64 0, ; id 0x0; name: 
		ptr @.MarshalMethodName.0_name; char* name
	} ; 0
], align 8

; get_function_pointer (uint32_t mono_image_index, uint32_t class_index, uint32_t method_token, void*& target_ptr)
@get_function_pointer = internal dso_local unnamed_addr global ptr null, align 4

; Functions

; Function attributes: "min-legal-vector-width"="0" mustprogress nofree norecurse nosync "no-trapping-math"="true" nounwind "stack-protector-buffer-size"="8" uwtable willreturn
define void @xamarin_app_init(ptr nocapture noundef readnone %env, ptr noundef %fn) local_unnamed_addr #0
{
	%fnIsNull = icmp eq ptr %fn, null
	br i1 %fnIsNull, label %1, label %2

1: ; preds = %0
	%putsResult = call noundef i32 @puts(ptr @.str.0)
	call void @abort()
	unreachable 

2: ; preds = %1, %0
	store ptr %fn, ptr @get_function_pointer, align 4, !tbaa !3
	ret void
}

; Strings
@.str.0 = private unnamed_addr constant [40 x i8] c"get_function_pointer MUST be specified\0A\00", align 1

;MarshalMethodName
@.MarshalMethodName.0_name = private unnamed_addr constant [1 x i8] c"\00", align 1

; External functions

; Function attributes: noreturn "no-trapping-math"="true" nounwind "stack-protector-buffer-size"="8"
declare void @abort() local_unnamed_addr #2

; Function attributes: nofree nounwind
declare noundef i32 @puts(ptr noundef) local_unnamed_addr #1
attributes #0 = { "min-legal-vector-width"="0" mustprogress nofree norecurse nosync "no-trapping-math"="true" nounwind "stack-protector-buffer-size"="8" "target-cpu"="generic" "target-features"="+armv7-a,+d32,+dsp,+fp64,+neon,+vfp2,+vfp2sp,+vfp3,+vfp3d16,+vfp3d16sp,+vfp3sp,-aes,-fp-armv8,-fp-armv8d16,-fp-armv8d16sp,-fp-armv8sp,-fp16,-fp16fml,-fullfp16,-sha2,-thumb-mode,-vfp4,-vfp4d16,-vfp4d16sp,-vfp4sp" uwtable willreturn }
attributes #1 = { nofree nounwind }
attributes #2 = { noreturn "no-trapping-math"="true" nounwind "stack-protector-buffer-size"="8" "target-cpu"="generic" "target-features"="+armv7-a,+d32,+dsp,+fp64,+neon,+vfp2,+vfp2sp,+vfp3,+vfp3d16,+vfp3d16sp,+vfp3sp,-aes,-fp-armv8,-fp-armv8d16,-fp-armv8d16sp,-fp-armv8sp,-fp16,-fp16fml,-fullfp16,-sha2,-thumb-mode,-vfp4,-vfp4d16,-vfp4d16sp,-vfp4sp" }

; Metadata
!llvm.module.flags = !{!0, !1, !7}
!0 = !{i32 1, !"wchar_size", i32 4}
!1 = !{i32 7, !"PIC Level", i32 2}
!llvm.ident = !{!2}
!2 = !{!"Xamarin.Android remotes/origin/release/8.0.2xx @ 96b6bb65e8736e45180905177aa343f0e1854ea3"}
!3 = !{!4, !4, i64 0}
!4 = !{!"any pointer", !5, i64 0}
!5 = !{!"omnipotent char", !6, i64 0}
!6 = !{!"Simple C++ TBAA"}
!7 = !{i32 1, !"min_enum_size", i32 4}
