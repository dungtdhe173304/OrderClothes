// com.example.orderclothes.activities.AdminManageProductsActivity
package com.example.orderclothes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.orderclothes.R;
import com.example.orderclothes.adapters.AdminProductAdapter;
import com.example.orderclothes.database.dao.CategoryDAO;
import com.example.orderclothes.database.dao.ProductDAO;
import com.example.orderclothes.models.Category;
import com.example.orderclothes.models.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.*;

public class AdminManageProductsActivity extends AppCompatActivity implements AdminProductAdapter.OnProductActionListener {

    private RecyclerView rvProducts;
    private EditText etSearch;
    private Spinner spnCategoryFilter, spnSortOption;
    private FloatingActionButton fabAddProduct;
    private AdminProductAdapter adapter;
    private List<Product> allProducts = new ArrayList<>();
    private List<Category> allCategories = new ArrayList<>();

    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_products);

        initViews();
        initObjects();
        setupRecyclerView();
        setupListeners();
        loadCategories();
        loadProducts();

        // Gán dữ liệu cho Spinner sắp xếp
        List<String> sortOptions = Arrays.asList(
                "Không sắp xếp",
                "Giá tăng dần",
                "Giá giảm dần",
                "Số lượng tăng dần",
                "Số lượng giảm dần"
        );
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, sortOptions
        );
        spnSortOption.setAdapter(sortAdapter);

    }

    private void initViews() {
        rvProducts = findViewById(R.id.rvProducts);
        etSearch = findViewById(R.id.etSearch);
        spnCategoryFilter = findViewById(R.id.spnCategoryFilter);
        spnSortOption = findViewById(R.id.spnSortOption);
        fabAddProduct = findViewById(R.id.fabAddProduct);
    }

    private void initObjects() {
        productDAO = new ProductDAO(this);
        categoryDAO = new CategoryDAO(this);
    }

    private void setupRecyclerView() {
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminProductAdapter(this, new ArrayList<>());
        adapter.setOnProductActionListener(this);
        rvProducts.setAdapter(adapter);
    }

    private void setupListeners() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterAndSortProducts();
            }
        });

        spnCategoryFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterAndSortProducts();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        spnSortOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterAndSortProducts();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        fabAddProduct.setOnClickListener(v -> {
            showAddEditProductDialog(null); // thêm mới
        });

    }

    private void loadCategories() {
        allCategories = categoryDAO.getAllCategories();
        List<String> categoryNames = new ArrayList<>();
        categoryNames.add("Tất cả");
        for (Category c : allCategories) {
            categoryNames.add(c.getCategoryName());
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categoryNames);
        spnCategoryFilter.setAdapter(spinnerAdapter);
    }

    private void loadProducts() {
      //  allProducts = productDAO.getAllActiveProducts(); // bạn có thể đổi thành getAllProducts() nếu cần cả đã ẩn
        allProducts = productDAO.getAllProducts(); // Hàm này phải trả về cả sản phẩm isActive = falseallProducts = productDAO.getAllProducts(); // Hàm này phải trả về cả sản phẩm isActive = false
        filterAndSortProducts();
    }

    private void filterAndSortProducts() {
        String query = etSearch.getText().toString().trim().toLowerCase();
        int categoryPos = spnCategoryFilter.getSelectedItemPosition();
        int sortPos = spnSortOption.getSelectedItemPosition();

        List<Product> filtered = new ArrayList<>();
        for (Product p : allProducts) {
            boolean matchSearch = p.getProductName().toLowerCase().contains(query)
                    || p.getBrand().toLowerCase().contains(query);
            boolean matchCategory = (categoryPos == 0) || (p.getCategoryId() == allCategories.get(categoryPos - 1).getCategoryId());
            if (matchSearch && matchCategory) {
                filtered.add(p);
            }
        }

        /// Sắp xếp theo tùy chọn
        switch (sortPos) {
            case 1: // Giá tăng dần
                filtered.sort(Comparator.comparingDouble(Product::getPrice));
                break;
            case 2: // Giá giảm dần
                filtered.sort((p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice()));
                break;
            case 3: // Số lượng tăng dần
                filtered.sort(Comparator.comparingInt(Product::getStockQuantity));
                break;
            case 4: // Số lượng giảm dần
                filtered.sort((p1, p2) -> Integer.compare(p2.getStockQuantity(), p1.getStockQuantity()));
                break;
            default:
                break;
        }

        adapter.updateProducts(filtered);
    }

    @Override
    public void onEdit(Product product) {
        showAddEditProductDialog(product); // sửa sản phẩm
    }


    @Override
    public void onDelete(Product product) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xoá")
                .setMessage("Bạn có chắc muốn xoá sản phẩm \"" + product.getProductName() + "\"?")
                .setPositiveButton("Xoá", (dialog, which) -> {
                    int result = productDAO.deleteProduct(product.getProductId());
                    if (result > 0) {
                        Toast.makeText(this, "Đã xoá sản phẩm", Toast.LENGTH_SHORT).show();
                        loadProducts(); // làm mới danh sách
                    } else {
                        Toast.makeText(this, "Lỗi khi xoá sản phẩm", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onToggleActive(Product product) {
        product.setActive(!product.isActive());
        productDAO.updateProduct(product);
        Toast.makeText(this, "Trạng thái đã được cập nhật", Toast.LENGTH_SHORT).show();
        loadProducts();
    }


    //

    private void showAddEditProductDialog(@Nullable Product productToEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_product, null);
        builder.setView(view);

        EditText etName = view.findViewById(R.id.etProductName);
        EditText etBrand = view.findViewById(R.id.etBrand);
        EditText etPrice = view.findViewById(R.id.etPrice);
        EditText etQty = view.findViewById(R.id.etQuantity);
        EditText etDesc = view.findViewById(R.id.etDescription);
        EditText etImageUrl = view.findViewById(R.id.etImageUrl);
        ImageView ivPreview = view.findViewById(R.id.ivPreviewImage);
        Spinner spnCategory = view.findViewById(R.id.spnCategory);

        // Load danh mục vào spinner
        List<String> categoryNames = new ArrayList<>();
        for (Category c : allCategories) categoryNames.add(c.getCategoryName());
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categoryNames);
        spnCategory.setAdapter(adapterSpinner);

        // Nếu là sửa thì hiển thị dữ liệu cũ
        if (productToEdit != null) {
            etName.setText(productToEdit.getProductName());
            etBrand.setText(productToEdit.getBrand());
            etPrice.setText(String.valueOf(productToEdit.getPrice()));
            etQty.setText(String.valueOf(productToEdit.getStockQuantity()));
            etDesc.setText(productToEdit.getDescription());
            etImageUrl.setText(productToEdit.getImageUrl());
            Glide.with(this).load(productToEdit.getImageUrl()).placeholder(R.drawable.ic_shopping_bag).into(ivPreview);

            // chọn đúng category
            for (int i = 0; i < allCategories.size(); i++) {
                if (allCategories.get(i).getCategoryId() == productToEdit.getCategoryId()) {
                    spnCategory.setSelection(i);
                    break;
                }
            }
        }

       /* // Xem trước ảnh khi nhập URL
        etImageUrl.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String url = etImageUrl.getText().toString().trim();
                if (!url.isEmpty()) {
                    Glide.with(this).load(url).placeholder(R.drawable.ic_shopping_bag).into(ivPreview);
                }
            }
        });*/
        // Xem trước ảnh khi nhập URL
        etImageUrl.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String url = s.toString().trim();
                if (!url.isEmpty()) {
                    Glide.with(AdminManageProductsActivity.this)
                            .load(url)
                            .placeholder(R.drawable.ic_shopping_bag)
                            .error(R.drawable.ic_error) // Thêm hình lỗi nếu có
                            .into(ivPreview);
                } else {
                    ivPreview.setImageResource(R.drawable.ic_shopping_bag); // reset lại nếu rỗng
                }
            }
        });


        builder.setTitle(productToEdit == null ? "Thêm sản phẩm" : "Sửa sản phẩm");

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String brand = etBrand.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            String imgUrl = etImageUrl.getText().toString().trim();
            int categoryId = allCategories.get(spnCategory.getSelectedItemPosition()).getCategoryId();

            double price;
            int quantity;
            try {
                price = Double.parseDouble(etPrice.getText().toString().trim());
                quantity = Integer.parseInt(etQty.getText().toString().trim());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Giá hoặc số lượng không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            if (productToEdit == null) {
                // Thêm mới
                Product p = new Product(name, desc, price, brand, quantity, categoryId, imgUrl, true);
                productDAO.insertProduct(p);
                Toast.makeText(this, "Đã thêm sản phẩm", Toast.LENGTH_SHORT).show();
            } else {
                // Cập nhật
                productToEdit.setProductName(name);
                productToEdit.setBrand(brand);
                productToEdit.setPrice(price);
                productToEdit.setStockQuantity(quantity);
                productToEdit.setDescription(desc);
                productToEdit.setImageUrl(imgUrl);
                productToEdit.setCategoryId(categoryId);
                productDAO.updateProduct(productToEdit);
                Toast.makeText(this, "Đã cập nhật sản phẩm", Toast.LENGTH_SHORT).show();
            }

            loadProducts(); // refresh danh sách
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

}
