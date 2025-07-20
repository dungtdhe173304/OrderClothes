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
import com.google.android.material.appbar.MaterialToolbar;
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

        // Gán Toolbar làm ActionBar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Hiện nút quay lại (nút back trên toolbar)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Gán icon quay lại và xử lý sự kiện click (đã đặt icon trong XML: app:navigationIcon="@drawable/ic_back")
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Khởi tạo các view và dữ liệu
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
        allProducts = productDAO.getAllProducts();
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
                        setResult(RESULT_OK);
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

        if (productToEdit != null) {
            etName.setText(productToEdit.getProductName());
            etBrand.setText(productToEdit.getBrand());
            etPrice.setText(String.valueOf(productToEdit.getPrice()));
            etQty.setText(String.valueOf(productToEdit.getStockQuantity()));
            etDesc.setText(productToEdit.getDescription());
            etImageUrl.setText(productToEdit.getImageUrl());
            Glide.with(this).load(productToEdit.getImageUrl()).placeholder(R.drawable.ic_shopping_bag).into(ivPreview);

            for (int i = 0; i < allCategories.size(); i++) {
                if (allCategories.get(i).getCategoryId() == productToEdit.getCategoryId()) {
                    spnCategory.setSelection(i);
                    break;
                }
            }
        }

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
                            .error(R.drawable.ic_error)
                            .into(ivPreview);
                } else {
                    ivPreview.setImageResource(R.drawable.ic_shopping_bag);
                }
            }
        });

        builder.setTitle(productToEdit == null ? "Thêm sản phẩm" : "Sửa sản phẩm");

        builder.setPositiveButton("Lưu", null); // gán sau để không đóng dialog nếu lỗi
        builder.setNegativeButton("Hủy", null);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(d -> {
            Button btnSave = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btnSave.setOnClickListener(v -> {
                String name = etName.getText().toString().trim();
                String brand = etBrand.getText().toString().trim();
                String desc = etDesc.getText().toString().trim();
                String imgUrl = etImageUrl.getText().toString().trim();
                String priceStr = etPrice.getText().toString().trim();
                String qtyStr = etQty.getText().toString().trim();
                int categoryId = allCategories.get(spnCategory.getSelectedItemPosition()).getCategoryId();

                // VALIDATION
                if (name.isEmpty()) {
                    Toast.makeText(this, "Tên sản phẩm không được để trống", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (brand.isEmpty()) {
                    Toast.makeText(this, "Thương hiệu không được để trống", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (desc.isEmpty()) {
                    Toast.makeText(this, "Mô tả không được để trống", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (imgUrl.isEmpty()) {
                    Toast.makeText(this, "URL ảnh không được để trống", Toast.LENGTH_SHORT).show();
                    return;
                }
                double price;
                try {
                    price = Double.parseDouble(priceStr);
                    if (price <= 0) {
                        Toast.makeText(this, "Giá phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Giá không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }
                int quantity;
                try {
                    quantity = Integer.parseInt(qtyStr);
                    if (quantity < 0) {
                        Toast.makeText(this, "Số lượng không được âm", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Số lượng không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (productToEdit == null) {
                    Product p = new Product(name, desc, price, brand, quantity, categoryId, imgUrl, true);
                    productDAO.insertProduct(p);
                    Toast.makeText(this, "Đã thêm sản phẩm", Toast.LENGTH_SHORT).show();
                } else {
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

                loadProducts();
                setResult(RESULT_OK);
                dialog.dismiss(); // chỉ đóng khi hợp lệ
            });
        });

        dialog.show();
    }


}
