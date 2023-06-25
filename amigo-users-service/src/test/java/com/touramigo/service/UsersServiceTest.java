package com.touramigo.service;

import com.touramigo.service.entity.global_config.Permission;
import com.touramigo.service.entity.global_config.PermissionGroup;
import com.touramigo.service.entity.global_config.Product;
import com.touramigo.service.entity.partner.Partner;
import com.touramigo.service.entity.partner.PartnerProduct;
import com.touramigo.service.entity.user.User;
import com.touramigo.service.exception.InvalidDataException;
import com.touramigo.service.model.AssignedProductModel;
import com.touramigo.service.model.CreateAssignedProductModel;
import com.touramigo.service.model.UserModel;
import com.touramigo.service.repository.*;
import com.touramigo.service.service.impl.UsersServiceImpl;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UsersServiceTest {

    @InjectMocks
    private UsersServiceImpl sut;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private ProductRepository productRepositoryMock;

    @Mock
    private PermissionRepository permissionRepositoryMock;

    @Mock
    private PartnerProductRepository partnerProductRepositoryMock;

    @Mock
    private UserProductRepository userProductRepositoryMock;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.userRepositoryMock = Mockito.mock(UserRepository.class);
        this.productRepositoryMock = Mockito.mock(ProductRepository.class);
        this.permissionRepositoryMock = Mockito.mock(PermissionRepository.class);
        this.partnerProductRepositoryMock = Mockito.mock(PartnerProductRepository.class);
        this.userProductRepositoryMock = Mockito.mock(UserProductRepository.class);
    }

    /*@Test
    public void testValidationExceptionOnCreateUpdateUser() {

        Principal mockPrincipal = Mockito.mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("amigo-admin");

        CreateUpdateUserModel model = new CreateUpdateUserModel();

        model.setPartnerId(UUID.randomUUID());

        assertThrows(InvalidDataException.class, () -> {
            sut.createUser(model, mockPrincipal);
        });

        assertThrows(InvalidDataException.class, () -> {
            sut.updateUser(model, UUID.randomUUID(), mockPrincipal);
        });
    }*/

    @Test
    public void testGetAllUsersForTourAmigoAdmin() {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("amigo-admin");
        when(userRepositoryMock.findByEmail(mockPrincipal.getName())).thenReturn(Optional.of(initUser()));
        List<User> users = this.initAllUsersList();
        when(userRepositoryMock.findAll()).thenReturn(users);
        //for tour amigo there should returned the same users list as mocked (all users)
        assertEquals(sut.getAllUsers().get(0).getId(), UserModel.create(users.get(0)).getId());
    }

    /*@Test
    public void testGetAllUsersForCompanyAdmin() {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("amigo-admin");
        when(userRepositoryMock.findByEmail(mockPrincipal.getName())).thenReturn(Optional.of(initUserWithEmptyRole()));
        // user without partner users should return empty list
        assertTrue(sut.getAllUsers(mockPrincipal).isEmpty());

        // user with partner that contain user should return the same mocked user
        User user = this.initUserWithEmptyRoleAndWithCompanyUsers();
        when(userRepositoryMock.findByEmail(mockPrincipal.getName())).thenReturn(Optional.of(user));
        assertEquals(sut.getAllUsers(mockPrincipal).get(0).getId(), UserModel.create(user).getId());
    }*/

    @Test
    public void modifyUserProducts() {
        User user = initUser();
        List<PartnerProduct> partnerProducts = initPartnerProducts();
        PartnerProduct partnerProduct = partnerProducts.get(0);
        UUID productIdToAssign = partnerProduct.getProduct().getId();
        List<Permission> permissions = partnerProduct.getPermissions().stream().collect(Collectors.toList());
        List<UUID> permissionIdToAssign = partnerProduct.getPermissions().stream().map(Permission::getId).collect(Collectors.toList());
        CreateAssignedProductModel assignProduct = new CreateAssignedProductModel(productIdToAssign, permissionIdToAssign);
        List<CreateAssignedProductModel> assignProducts = new ArrayList<>();
        assignProducts.add(assignProduct);

        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));
        when(partnerProductRepositoryMock.findByPartnerId(user.getPartner().getId())).thenReturn(partnerProducts);
        when(productRepositoryMock.findById(productIdToAssign)).thenReturn(Optional.of(partnerProduct.getProduct()));
        when(permissionRepositoryMock.findAll(permissionIdToAssign)).thenReturn(permissions);

        Set<AssignedProductModel> result = sut.modifyUserProducts(user.getId(), assignProducts);
        assertEquals(result.size(), 1);
        List<AssignedProductModel> resultList = new ArrayList<>(result);
        assertEquals(resultList.get(0).getProduct().getId(), productIdToAssign);
        assertEquals(resultList.get(0).getPermissions().size(), permissions.size());
    }

    @Test
    public void modifyUserProductsNotAssignedToPartner() {
        User user = initUser();
        List<PartnerProduct> partnerProducts = initPartnerProducts();
        PartnerProduct partnerProduct = partnerProducts.get(0);
        UUID productIdToAssign = partnerProduct.getProduct().getId();
        List<Permission> permissions = partnerProduct.getPermissions().stream().collect(Collectors.toList());
        List<UUID> permissionIdToAssign = partnerProduct.getPermissions().stream().map(Permission::getId).collect(Collectors.toList());
        CreateAssignedProductModel assignProduct = new CreateAssignedProductModel(productIdToAssign, permissionIdToAssign);
        List<CreateAssignedProductModel> assignProducts = new ArrayList<>();
        assignProducts.add(assignProduct);

        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));
        when(partnerProductRepositoryMock.findByPartnerId(user.getPartner().getId())).thenReturn(new ArrayList<>());
        when(productRepositoryMock.findById(productIdToAssign)).thenReturn(Optional.of(partnerProduct.getProduct()));
        when(permissionRepositoryMock.findAll(permissionIdToAssign)).thenReturn(permissions);

        assertThrows(InvalidDataException.class, () -> sut.modifyUserProducts(user.getId(), assignProducts));
    }

    private List<User> initAllUsersList() {
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@test.com");
        user.setFullName("test");
        user.setJobTitle("test");
        user.setContactNumber("test");
        user.setEnabled(true);

        users.add(user);
        return users;
    }

    private User initUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        Partner partner = new Partner();
        partner.setId(UUID.randomUUID());
        user.setPartner(partner);
        return user;
    }

    private List<PartnerProduct> initPartnerProducts() {
        Product product = new Product();
        product.setId(UUID.randomUUID());

        PermissionGroup defaultGroup = new PermissionGroup();
        Permission permission = new Permission();
        permission.setId(UUID.randomUUID());
        permission.setProduct(product);
        permission.setGroup(defaultGroup);
        Set<Permission> permissions = new HashSet<>();
        permissions.add(permission);

        PartnerProduct partnerProduct = new PartnerProduct();
        partnerProduct.setProduct(product);
        partnerProduct.setPermissions(permissions);

        List<PartnerProduct> partnerProducts = new ArrayList<>();
        partnerProducts.add(partnerProduct);
        return partnerProducts;
    }
}
