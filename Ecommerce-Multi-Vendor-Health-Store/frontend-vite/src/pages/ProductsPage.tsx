import { useState, useEffect, useMemo } from "react"
import { motion } from "framer-motion"
import { 
  Search, 
  Grid3X3, 
  List, 
  Star, 
  Heart, 
  ShoppingCart,
  SlidersHorizontal,
  Loader2
} from "lucide-react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Card, CardContent, CardFooter } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { cn, formatPrice } from "@/lib/utils"
import { apiService } from "@/services/api"
import { Product as ProductType, Category } from "@/types/api"

// Extended Product type with UI-specific properties
interface Product extends ProductType {
  originalPrice?: number;
  inStock: boolean;
  featured: boolean;
  imageUrl: string;
  category: string;
  rating: number;
  reviews: number;
}
  {
    id: "2",
    name: "Organic Protein Powder",
    description: "Plant-based protein powder with natural ingredients for muscle building",
    price: 49.99,
    originalPrice: 59.99,
    rating: 4.9,
    reviews: 89,
    imageUrl: "https://images.unsplash.com/photo-1593095948071-474c5cc2989d?w=400&h=400&fit=crop",
    category: "Protein",
    inStock: true,
    featured: true,
  },
  {
    id: "3",
    name: "Omega-3 Fish Oil",
    description: "Pure omega-3 supplement for heart and brain health support",
    price: 34.99,
    originalPrice: 44.99,
    rating: 4.7,
    reviews: 156,
    imageUrl: "https://images.unsplash.com/photo-1559757175-0eb30cd8c063?w=400&h=400&fit=crop",
    category: "Supplements",
    inStock: true,
    featured: false,
  },
  {
    id: "4",
    name: "Multivitamin Complex",
    description: "Complete daily multivitamin for overall wellness and energy",
    price: 1299,
    originalPrice: 1799,
    rating: 4.6,
    reviews: 203,
    imageUrl: "https://images.unsplash.com/photo-1550572017-edd951aa8f72?w=400&h=400&fit=crop",
    category: "Vitamins",
    inStock: true,
    featured: false,
  },
  {
    id: "5",
    name: "Probiotics Capsules",
    description: "Advanced probiotic formula for digestive health and immunity",
    price: 1999,
    originalPrice: 2499,
    rating: 4.5,
    reviews: 78,
    imageUrl: "https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=400&h=400&fit=crop",
    category: "Supplements",
    inStock: true,
    featured: false,
  },
  {
    id: "6",
    name: "Collagen Peptides",
    description: "Hydrolyzed collagen for skin, hair, and joint health",
    price: 2299,
    originalPrice: 2799,
    rating: 4.4,
    reviews: 92,
    imageUrl: "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400&h=400&fit=crop",
    category: "Beauty",
    inStock: false,
    featured: false,
  },
]

const categories = [
  { name: "All", count: products.length },
  { name: "Vitamins", count: products.filter(p => p.category === "Vitamins").length },
  { name: "Protein", count: products.filter(p => p.category === "Protein").length },
  { name: "Supplements", count: products.filter(p => p.category === "Supplements").length },
  { name: "Beauty", count: products.filter(p => p.category === "Beauty").length },
]

const sortOptions = [
  { label: "Featured", value: "featured" },
  { label: "Price: Low to High", value: "price-asc" },
  { label: "Price: High to Low", value: "price-desc" },
  { label: "Customer Rating", value: "rating" },
  { label: "Newest", value: "newest" },
]

export default function ProductsPage() {
  const [products, setProducts] = useState<Product[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [categories, setCategories] = useState<Category[]>([])
  const [view, setView] = useState<"grid" | "list">("grid")
  const [searchQuery, setSearchQuery] = useState("")
  const [priceRange, setPriceRange] = useState<[number, number]>([0, 10000])
  const [selectedCategories, setSelectedCategories] = useState<number[]>([])
  const [sortBy, setSortBy] = useState<"relevance" | "price_asc" | "price_desc" | "rating">("relevance")

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true)
        setError(null)
        
        // Fetch products with filters
        const productsResponse = await apiService.getProducts({
          page: 0,
          size: 50,
          sortBy: sortBy === 'relevance' ? 'newest' : 
                 sortBy === 'price_asc' ? 'price,asc' : 
                 sortBy === 'price_desc' ? 'price,desc' : 'rating,desc',
          minPrice: priceRange[0],
          maxPrice: priceRange[1],
          query: searchQuery || undefined,
          categoryId: selectedCategories.length > 0 ? selectedCategories[0] : undefined
        })
        
        // Transform API data to match our UI requirements
        const transformedProducts = productsResponse.content.map(product => ({
          ...product,
          originalPrice: product.originalPrice || 0,
          inStock: product.stock > 0,
          featured: product.featured || false,
          imageUrl: product.imageUrl || 'https://via.placeholder.com/300',
          category: product.category?.name || 'Uncategorized',
          rating: product.rating || 0,
          reviews: product.reviewCount || 0
        }))
        
        setProducts(transformedProducts)
        
        // Fetch categories if not already loaded
        if (categories.length === 0) {
          const categoriesResponse = await apiService.getCategories()
          setCategories(categoriesResponse)
        }
        
      } catch (error) {
        console.error('Error fetching data:', error)
        setError('Failed to load products. Please try again later.')
      } finally {
        setLoading(false)
      }
    }
    
    const debounceTimer = setTimeout(() => {
      fetchData()
    }, 300) // Debounce search to avoid too many API calls
    
    return () => clearTimeout(debounceTimer)
  }, [searchQuery, priceRange, selectedCategories, sortBy, categories.length])

  // Handle loading state
  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[60vh]">
        <Loader2 className="h-12 w-12 animate-spin text-primary" />
      </div>
    )
  }

  // Handle error state
  if (error) {
    return (
      <div className="container mx-auto px-4 py-12">
        <div className="text-center text-red-500">{error}</div>
        <div className="mt-4 text-center">
          <Button onClick={() => window.location.reload()}>Retry</Button>
        </div>
      </div>
    )
  }

  const filteredProducts = useMemo(() => {
    let filtered = products

    // Filter by category
    if (selectedCategories.length > 0) {
      filtered = filtered.filter((product: Product) => selectedCategories.includes(product.category))
    }

    // Filter by search query
    if (searchQuery) {
      filtered = filtered.filter(product =>
        product.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
        product.description.toLowerCase().includes(searchQuery.toLowerCase())
      )
    }

    // Sort products
    switch (sortBy) {
      case "price-asc":
        filtered.sort((a, b) => a.price - b.price)
        break
      case "price-desc":
        filtered.sort((a, b) => b.price - a.price)
        break
      case "rating":
        filtered.sort((a, b) => b.rating - a.rating)
        break
      case "featured":
        filtered.sort((a, b) => (b.featured ? 1 : 0) - (a.featured ? 1 : 0))
        break
      default:
        break
    }

    return filtered
  }, [selectedCategory, searchQuery, sortBy])

  const containerVariants = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: {
        staggerChildren: 0.1,
      },
    },
  }

  const itemVariants = {
    hidden: { opacity: 0, y: 20 },
    visible: {
      opacity: 1,
      y: 0,
      transition: {
        duration: 0.5,
      },
    },
  }

  return (
    <div className="min-h-screen bg-background">
      {/* Header */}
      <div className="bg-muted/30 border-b">
        <div className="container mx-auto px-4 py-8">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="text-center"
          >
            <h1 className="text-4xl font-bold mb-4">Health Products</h1>
            <p className="text-muted-foreground max-w-2xl mx-auto">
              Discover our comprehensive collection of premium health supplements and wellness products
            </p>
          </motion.div>
        </div>
      </div>

      <div className="container mx-auto px-4 py-8">
        <div className="flex flex-col lg:flex-row gap-8">
          {/* Sidebar Filters */}
          <motion.div
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            className={cn(
              "lg:w-64 space-y-6",
              showFilters ? "block" : "hidden lg:block"
            )}
          >
            {/* Search */}
            <div>
              <h3 className="font-semibold mb-3">Search</h3>
              <div className="relative">
                <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
                <Input
                  type="search"
                  placeholder="Search products..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="pl-10"
                />
              </div>
            </div>

            {/* Categories */}
            <div>
              <h3 className="font-semibold mb-3">Categories</h3>
              <div className="space-y-2">
                {categories.map((category) => (
                  <button
                    key={category.name}
                    onClick={() => setSelectedCategory(category.name)}
                    className={cn(
                      "w-full text-left px-3 py-2 rounded-md text-sm transition-colors",
                      selectedCategory === category.name
                        ? "bg-primary text-primary-foreground"
                        : "hover:bg-muted"
                    )}
                  >
                    <div className="flex justify-between items-center">
                      <span>{category.name}</span>
                      <span className="text-xs opacity-70">({category.count})</span>
                    </div>
                  </button>
                ))}
              </div>
            </div>

            {/* Price Range */}
            <div>
              <h3 className="font-semibold mb-3">Price Range</h3>
              <div className="space-y-2">
                <div className="flex items-center space-x-2">
                  <Input type="number" placeholder="Min" className="text-sm" />
                  <span className="text-muted-foreground">-</span>
                  <Input type="number" placeholder="Max" className="text-sm" />
                </div>
                <Button variant="outline" size="sm" className="w-full">
                  Apply
                </Button>
              </div>
            </div>

            {/* Rating Filter */}
            <div>
              <h3 className="font-semibold mb-3">Customer Rating</h3>
              <div className="space-y-2">
                {[4, 3, 2, 1].map((rating) => (
                  <button
                    key={rating}
                    className="flex items-center space-x-2 w-full text-left px-3 py-2 rounded-md hover:bg-muted transition-colors"
                  >
                    <div className="flex">
                      {[...Array(5)].map((_, i) => (
                        <Star
                          key={i}
                          className={cn(
                            "h-4 w-4",
                            i < rating
                              ? "text-yellow-400 fill-current"
                              : "text-gray-300"
                          )}
                        />
                      ))}
                    </div>
                    <span className="text-sm">& Up</span>
                  </button>
                ))}
              </div>
            </div>
          </motion.div>

          {/* Main Content */}
          <div className="flex-1">
            {/* Toolbar */}
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-6"
            >
              <div className="flex items-center gap-4">
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => setShowFilters(!showFilters)}
                  className="lg:hidden"
                >
                  <SlidersHorizontal className="h-4 w-4 mr-2" />
                  Filters
                </Button>
                <p className="text-sm text-muted-foreground">
                  Showing {filteredProducts.length} of {products.length} products
                </p>
              </div>

              <div className="flex items-center gap-4">
                {/* Sort Dropdown */}
                <div className="flex items-center gap-2">
                  <span className="text-sm text-muted-foreground">Sort by:</span>
                  <select
                    value={sortBy}
                    onChange={(e) => setSortBy(e.target.value)}
                    className="text-sm border rounded-md px-3 py-1 bg-background"
                  >
                    {sortOptions.map((option) => (
                      <option key={option.value} value={option.value}>
                        {option.label}
                      </option>
                    ))}
                  </select>
                </div>

                {/* View Mode Toggle */}
                <div className="flex items-center border rounded-md">
                  <Button
                    variant={viewMode === "grid" ? "default" : "ghost"}
                    size="sm"
                    onClick={() => setViewMode("grid")}
                    className="rounded-r-none"
                  >
                    <Grid3X3 className="h-4 w-4" />
                  </Button>
                  <Button
                    variant={viewMode === "list" ? "default" : "ghost"}
                    size="sm"
                    onClick={() => setViewMode("list")}
                    className="rounded-l-none"
                  >
                    <List className="h-4 w-4" />
                  </Button>
                </div>
              </div>
            </motion.div>

            {/* Products Grid */}
            <motion.div
              variants={containerVariants}
              initial="hidden"
              animate="visible"
              className={cn(
                "grid gap-6",
                viewMode === "grid"
                  ? "grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4"
                  : "grid-cols-1"
              )}
            >
              {filteredProducts.map((product) => (
                <motion.div key={product.id} variants={itemVariants}>
                  <Card className="group overflow-hidden h-full hover:-translate-y-1 transition-transform duration-300">
                    <div className="relative">
                      <div className={cn(
                        "overflow-hidden",
                        viewMode === "grid" ? "aspect-square" : "aspect-video sm:aspect-square md:aspect-video"
                      )}>
                        <img
                          src={product.imageUrl}
                          alt={product.name}
                          className="w-full h-full object-cover transition-transform duration-300 group-hover:scale-110"
                        />
                      </div>
                      <div className="absolute top-2 right-2 flex flex-col gap-2">
                        {product.originalPrice > product.price && (
                          <Badge variant="destructive" className="text-xs">
                            {Math.round(((product.originalPrice - product.price) / product.originalPrice) * 100)}% OFF
                          </Badge>
                        )}
                        {product.featured && (
                          <Badge variant="secondary" className="text-xs">
                            Featured
                          </Badge>
                        )}
                        <Button
                          size="icon"
                          variant="ghost"
                          className="h-8 w-8 bg-white/80 hover:bg-white"
                        >
                          <Heart className="h-4 w-4" />
                        </Button>
                      </div>
                      {!product.inStock && (
                        <div className="absolute inset-0 bg-black/50 flex items-center justify-center">
                          <Badge variant="destructive">Out of Stock</Badge>
                        </div>
                      )}
                    </div>

                    <CardContent className="p-4">
                      <div className="mb-2">
                        <Badge variant="secondary" className="text-xs">
                          {product.category}
                        </Badge>
                      </div>
                      <h3 className="font-semibold text-sm mb-2 line-clamp-2">
                        {product.name}
                      </h3>
                      <p className="text-xs text-muted-foreground mb-3 line-clamp-2">
                        {product.description}
                      </p>
                      
                      <div className="flex items-center gap-1 mb-3">
                        <div className="flex">
                          {[...Array(5)].map((_, i) => (
                            <Star
                              key={i}
                              className={cn(
                                "h-3 w-3",
                                i < Math.floor(product.rating)
                                  ? "text-yellow-400 fill-current"
                                  : "text-gray-300"
                              )}
                            />
                          ))}
                        </div>
                        <span className="text-xs text-muted-foreground">
                          ({product.reviews})
                        </span>
                      </div>

                      <div className="flex items-center justify-between">
                        <div className="flex items-center gap-2">
                          <span className="font-bold text-primary">
                            {formatPrice(product.price)}
                          </span>
                          {product.originalPrice > product.price && (
                            <span className="text-xs text-muted-foreground line-through">
                              {formatPrice(product.originalPrice)}
                            </span>
                          )}
                        </div>
                      </div>
                    </CardContent>

                    <CardFooter className="p-4 pt-0">
                      <Button 
                        className="w-full group" 
                        size="sm"
                        disabled={!product.inStock}
                      >
                        <ShoppingCart className="mr-2 h-4 w-4" />
                        {product.inStock ? "Add to Cart" : "Out of Stock"}
                      </Button>
                    </CardFooter>
                  </Card>
                </motion.div>
              ))}
            </motion.div>

            {/* Load More / Pagination */}
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              transition={{ delay: 0.5 }}
              className="mt-12 text-center"
            >
              <Button variant="outline" size="lg">
                Load More Products
              </Button>
            </motion.div>
          </div>
        </div>
      </div>
    </div>
  )
}
