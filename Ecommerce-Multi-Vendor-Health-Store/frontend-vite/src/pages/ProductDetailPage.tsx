import { useState } from "react"
import { useParams } from "react-router-dom"
import { motion } from "framer-motion"
import { 
  Star, 
  Heart, 
  ShoppingCart, 
  Plus, 
  Minus, 
  Share2, 
  Truck, 
  Shield, 
  RefreshCw
} from "lucide-react"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { cn, formatPrice } from "@/lib/utils"

// Mock product data - replace with actual API call
const productData = {
  id: "1",
  name: "Premium Vitamin D3",
  description: "High-quality vitamin D3 supplement for immune support and bone health. Our premium formula provides 2000 IU of vitamin D3 per capsule, sourced from the finest ingredients to ensure maximum absorption and effectiveness.",
  longDescription: "This premium vitamin D3 supplement is carefully formulated to support your immune system and promote healthy bones. Each capsule contains 2000 IU of cholecalciferol (vitamin D3), the most bioavailable form of vitamin D. Our advanced formula includes natural ingredients that enhance absorption, ensuring you get the maximum benefit from each dose.",
  price: 29.99,
  originalPrice: 39.99,
  rating: 4.8,
  reviews: 124,
  images: [
    "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=600&h=600&fit=crop",
    "https://images.unsplash.com/photo-1559757175-0eb30cd8c063?w=600&h=600&fit=crop",
    "https://images.unsplash.com/photo-1550572017-edd951aa8f72?w=600&h=600&fit=crop",
  ],
  category: "Vitamins",
  inStock: true,
  stockQuantity: 45,
  features: [
    "2000 IU of Vitamin D3 per capsule",
    "Enhanced absorption formula",
    "Third-party tested for purity",
    "Non-GMO and gluten-free",
    "90 capsules per bottle",
  ],
  ingredients: "Vitamin D3 (as Cholecalciferol), Microcrystalline Cellulose, Vegetable Capsule (Hypromellose), Rice Flour, Magnesium Stearate",
  directions: "Take 1 capsule daily with food or as directed by your healthcare professional.",
  warnings: "Consult your physician before use if pregnant, nursing, or taking medications.",
}

const reviews = [
  {
    id: 1,
    user: "Sarah M.",
    rating: 5,
    date: "2024-01-15",
    comment: "Excellent quality vitamin D3! I've been taking it for 3 months and my energy levels have improved significantly.",
    verified: true,
  },
  {
    id: 2,
    user: "Mike R.",
    rating: 4,
    date: "2024-01-10",
    comment: "Good product, easy to swallow capsules. Will definitely reorder.",
    verified: true,
  },
  {
    id: 3,
    user: "Jennifer L.",
    rating: 5,
    date: "2024-01-05",
    comment: "My doctor recommended vitamin D3 and this brand has been perfect. Great value for money.",
    verified: true,
  },
]

export function ProductDetailPage() {
  const { id } = useParams()
  const [selectedImageIndex, setSelectedImageIndex] = useState(0)
  const [quantity, setQuantity] = useState(1)
  const [activeTab, setActiveTab] = useState("description")

  const handleQuantityChange = (change: number) => {
    setQuantity(Math.max(1, Math.min(productData.stockQuantity, quantity + change)))
  }

  const handleAddToCart = () => {
    // TODO: Implement add to cart functionality
    console.log("Adding to cart:", { productId: id, quantity })
  }

  const handleAddToWishlist = () => {
    // TODO: Implement add to wishlist functionality
    console.log("Adding to wishlist:", id)
  }

  return (
    <div className="min-h-screen bg-background">
      <div className="container mx-auto px-4 py-8">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="grid grid-cols-1 lg:grid-cols-2 gap-12"
        >
          {/* Product Images */}
          <div className="space-y-4">
            <motion.div
              initial={{ opacity: 0, scale: 0.9 }}
              animate={{ opacity: 1, scale: 1 }}
              className="relative aspect-square overflow-hidden rounded-lg bg-muted"
            >
              <img
                src={productData.images[selectedImageIndex]}
                alt={productData.name}
                className="w-full h-full object-cover"
              />
              <div className="absolute top-4 right-4 flex flex-col gap-2">
                <Button
                  size="icon"
                  variant="ghost"
                  className="bg-white/80 hover:bg-white"
                  onClick={handleAddToWishlist}
                >
                  <Heart className="h-5 w-5" />
                </Button>
                <Button
                  size="icon"
                  variant="ghost"
                  className="bg-white/80 hover:bg-white"
                >
                  <Share2 className="h-5 w-5" />
                </Button>
              </div>
            </motion.div>

            {/* Image Thumbnails */}
            <div className="flex gap-2">
              {productData.images.map((image, index) => (
                <button
                  key={index}
                  onClick={() => setSelectedImageIndex(index)}
                  className={cn(
                    "relative aspect-square w-20 overflow-hidden rounded-md border-2 transition-colors",
                    selectedImageIndex === index
                      ? "border-primary"
                      : "border-transparent hover:border-muted-foreground"
                  )}
                >
                  <img
                    src={image}
                    alt={`${productData.name} ${index + 1}`}
                    className="w-full h-full object-cover"
                  />
                </button>
              ))}
            </div>
          </div>

          {/* Product Info */}
          <div className="space-y-6">
            <motion.div
              initial={{ opacity: 0, x: 20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: 0.2 }}
            >
              <div className="mb-4">
                <Badge variant="secondary" className="mb-2">
                  {productData.category}
                </Badge>
                <h1 className="text-3xl font-bold mb-2">{productData.name}</h1>
                <p className="text-muted-foreground">{productData.description}</p>
              </div>

              {/* Rating */}
              <div className="flex items-center gap-2 mb-4">
                <div className="flex">
                  {[...Array(5)].map((_, i) => (
                    <Star
                      key={i}
                      className={cn(
                        "h-5 w-5",
                        i < Math.floor(productData.rating)
                          ? "text-yellow-400 fill-current"
                          : "text-gray-300"
                      )}
                    />
                  ))}
                </div>
                <span className="font-medium">{productData.rating}</span>
                <span className="text-muted-foreground">({productData.reviews} reviews)</span>
              </div>

              {/* Price */}
              <div className="flex items-center gap-4 mb-6">
                <span className="text-3xl font-bold text-primary">
                  {formatPrice(productData.price)}
                </span>
                {productData.originalPrice > productData.price && (
                  <>
                    <span className="text-xl text-muted-foreground line-through">
                      {formatPrice(productData.originalPrice)}
                    </span>
                    <Badge variant="destructive">
                      {Math.round(((productData.originalPrice - productData.price) / productData.originalPrice) * 100)}% OFF
                    </Badge>
                  </>
                )}
              </div>

              {/* Stock Status */}
              <div className="mb-6">
                {productData.inStock ? (
                  <div className="flex items-center gap-2 text-green-600">
                    <div className="w-2 h-2 bg-green-600 rounded-full"></div>
                    <span className="text-sm font-medium">In Stock ({productData.stockQuantity} available)</span>
                  </div>
                ) : (
                  <div className="flex items-center gap-2 text-red-600">
                    <div className="w-2 h-2 bg-red-600 rounded-full"></div>
                    <span className="text-sm font-medium">Out of Stock</span>
                  </div>
                )}
              </div>

              {/* Quantity Selector */}
              <div className="flex items-center gap-4 mb-6">
                <span className="text-sm font-medium">Quantity:</span>
                <div className="flex items-center border rounded-md">
                  <Button
                    variant="ghost"
                    size="icon"
                    onClick={() => handleQuantityChange(-1)}
                    disabled={quantity <= 1}
                    className="h-10 w-10"
                  >
                    <Minus className="h-4 w-4" />
                  </Button>
                  <span className="px-4 py-2 min-w-[3rem] text-center">{quantity}</span>
                  <Button
                    variant="ghost"
                    size="icon"
                    onClick={() => handleQuantityChange(1)}
                    disabled={quantity >= productData.stockQuantity}
                    className="h-10 w-10"
                  >
                    <Plus className="h-4 w-4" />
                  </Button>
                </div>
              </div>

              {/* Add to Cart */}
              <div className="flex gap-4 mb-8">
                <Button
                  size="lg"
                  className="flex-1"
                  onClick={handleAddToCart}
                  disabled={!productData.inStock}
                >
                  <ShoppingCart className="mr-2 h-5 w-5" />
                  Add to Cart
                </Button>
                <Button size="lg" variant="outline">
                  Buy Now
                </Button>
              </div>

              {/* Features */}
              <div className="space-y-3">
                <h3 className="font-semibold">Key Features:</h3>
                <ul className="space-y-2">
                  {productData.features.map((feature, index) => (
                    <li key={index} className="flex items-start gap-2 text-sm">
                      <div className="w-1.5 h-1.5 bg-primary rounded-full mt-2 flex-shrink-0"></div>
                      <span>{feature}</span>
                    </li>
                  ))}
                </ul>
              </div>

              {/* Shipping Info */}
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 pt-6 border-t">
                <div className="flex items-center gap-2 text-sm">
                  <Truck className="h-4 w-4 text-primary" />
                  <span>Free shipping over $50</span>
                </div>
                <div className="flex items-center gap-2 text-sm">
                  <Shield className="h-4 w-4 text-primary" />
                  <span>Quality guaranteed</span>
                </div>
                <div className="flex items-center gap-2 text-sm">
                  <RefreshCw className="h-4 w-4 text-primary" />
                  <span>30-day returns</span>
                </div>
              </div>
            </motion.div>
          </div>
        </motion.div>

        {/* Product Details Tabs */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.4 }}
          className="mt-16"
        >
          <div className="border-b">
            <div className="flex space-x-8">
              {[
                { id: "description", label: "Description" },
                { id: "ingredients", label: "Ingredients" },
                { id: "directions", label: "Directions" },
                { id: "reviews", label: `Reviews (${reviews.length})` },
              ].map((tab) => (
                <button
                  key={tab.id}
                  onClick={() => setActiveTab(tab.id)}
                  className={cn(
                    "py-4 px-1 border-b-2 font-medium text-sm transition-colors",
                    activeTab === tab.id
                      ? "border-primary text-primary"
                      : "border-transparent text-muted-foreground hover:text-foreground"
                  )}
                >
                  {tab.label}
                </button>
              ))}
            </div>
          </div>

          <div className="py-8">
            {activeTab === "description" && (
              <div className="prose max-w-none">
                <p className="text-muted-foreground leading-relaxed">
                  {productData.longDescription}
                </p>
              </div>
            )}

            {activeTab === "ingredients" && (
              <div>
                <h3 className="font-semibold mb-4">Ingredients</h3>
                <p className="text-muted-foreground">{productData.ingredients}</p>
              </div>
            )}

            {activeTab === "directions" && (
              <div className="space-y-4">
                <div>
                  <h3 className="font-semibold mb-2">Directions for Use</h3>
                  <p className="text-muted-foreground">{productData.directions}</p>
                </div>
                <div>
                  <h3 className="font-semibold mb-2">Warnings</h3>
                  <p className="text-muted-foreground text-sm">{productData.warnings}</p>
                </div>
              </div>
            )}

            {activeTab === "reviews" && (
              <div className="space-y-6">
                {reviews.map((review) => (
                  <Card key={review.id}>
                    <CardContent className="p-6">
                      <div className="flex items-start justify-between mb-4">
                        <div>
                          <div className="flex items-center gap-2 mb-1">
                            <span className="font-medium">{review.user}</span>
                            {review.verified && (
                              <Badge variant="secondary" className="text-xs">
                                Verified Purchase
                              </Badge>
                            )}
                          </div>
                          <div className="flex items-center gap-2">
                            <div className="flex">
                              {[...Array(5)].map((_, i) => (
                                <Star
                                  key={i}
                                  className={cn(
                                    "h-4 w-4",
                                    i < review.rating
                                      ? "text-yellow-400 fill-current"
                                      : "text-gray-300"
                                  )}
                                />
                              ))}
                            </div>
                            <span className="text-sm text-muted-foreground">
                              {new Date(review.date).toLocaleDateString()}
                            </span>
                          </div>
                        </div>
                      </div>
                      <p className="text-muted-foreground">{review.comment}</p>
                    </CardContent>
                  </Card>
                ))}
              </div>
            )}
          </div>
        </motion.div>
      </div>
    </div>
  )
}
