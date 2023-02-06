package com.loyverse.dashboard.base.products;


public abstract class ProductClassWrapper {
    public static class Item extends ProductClassWrapper {
        private ProductItem wares;

        public Item(ProductItem wares) {
            this.wares = wares;
        }

        public ProductItem getProduct() {
            return wares;
        }
    }

    public static class Variant extends ProductClassWrapper {
        private ProductItem.Variant variant;

        public Variant(ProductItem.Variant variant) {
            this.variant = variant;
        }

        public ProductItem.Variant getVariant() {
            return variant;
        }
    }
}