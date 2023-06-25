package com.touramigo.service.util;

import com.touramigo.service.entity.global_config.Permission;
import com.touramigo.service.entity.partner.OperationalGroup;
import com.touramigo.service.entity.partner.Partner;
import com.touramigo.service.entity.partner.PartnerProduct;
import com.touramigo.service.entity.user.User;
import com.touramigo.service.entity.user.UserProduct;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.when;
import static org.hamcrest.MatcherAssert.assertThat;

public class SecurityUtilTest {
    @Test
    public void verifyUserPermissions() {
        Permission permission = new Permission();
        permission.setCodeKey("test");
        Set<Permission> permissions = new HashSet<>();
        permissions.add(permission);

        PartnerProduct product = Mockito.mock(PartnerProduct.class);
        when(product.getPermissions()).thenReturn(permissions);
        List<PartnerProduct> products = new ArrayList<>();
        products.add(product);

        UserProduct userProduct = Mockito.mock(UserProduct.class);
        when(userProduct.getPermissions()).thenReturn(permissions);
        List<UserProduct> userProducts = new ArrayList<>();
        userProducts.add(userProduct);

        Partner partner = Mockito.mock(Partner.class);
        when(partner.getPartnerProducts()).thenReturn(products);

        User user = Mockito.mock(User.class);
        when(user.getPartner()).thenReturn(partner);
        when(user.getUserProducts()).thenReturn(userProducts);
        when(user.getOperationalGroups()).thenReturn(new ArrayList<>());

        List<String> authorities = SecurityUtil.getUsersPermissionsCodeKeys(user);
        assertThat(authorities.size(), is(1));
        assertThat(authorities.get(0), equalTo("test"));
    }

    @Test
    public void verifyPartnerPermissions() {
        Permission permission = new Permission();
        permission.setCodeKey("test");
        Set<Permission> permissions = new HashSet<>();
        permissions.add(permission);

        UserProduct userProduct = Mockito.mock(UserProduct.class);
        when(userProduct.getPermissions()).thenReturn(permissions);
        List<UserProduct> userProducts = new ArrayList<>();
        userProducts.add(userProduct);

        Partner partner = Mockito.mock(Partner.class);
        when(partner.getPartnerProducts()).thenReturn(new ArrayList<>());

        User user = Mockito.mock(User.class);
        when(user.getPartner()).thenReturn(partner);
        when(user.getUserProducts()).thenReturn(userProducts);
        when(user.getOperationalGroups()).thenReturn(new ArrayList<>());

        List<String> authorities = SecurityUtil.getUsersPermissionsCodeKeys(user);
        assertThat(authorities.size(), is(0));
    }

    @Test
    public void verifyOperationGroupPermissions() {
        Permission permission1 = new Permission();
        permission1.setCodeKey("test1");
        Permission permission2 = new Permission();
        permission2.setCodeKey("test2");

        Set<Permission> partnerPermissions = new HashSet<>();
        partnerPermissions.add(permission1);
        partnerPermissions.add(permission2);

        Set<Permission> userPermissions = new HashSet<>();
        userPermissions.add(permission1);

        List<Permission> oGroupPermissions = new ArrayList<>();
        oGroupPermissions.add(permission2);

        PartnerProduct product = Mockito.mock(PartnerProduct.class);
        when(product.getPermissions()).thenReturn(partnerPermissions);
        List<PartnerProduct> products = new ArrayList<>();
        products.add(product);

        UserProduct userProduct = Mockito.mock(UserProduct.class);
        when(userProduct.getPermissions()).thenReturn(userPermissions);
        List<UserProduct> userProducts = new ArrayList<>();
        userProducts.add(userProduct);

        Partner partner = Mockito.mock(Partner.class);
        when(partner.getPartnerProducts()).thenReturn(products);

        OperationalGroup oGroup = Mockito.mock(OperationalGroup.class);
        when(oGroup.getPermissions()).thenReturn(oGroupPermissions);
        List<OperationalGroup> operationalGroups = new ArrayList<>();
        operationalGroups.add(oGroup);

        User user = Mockito.mock(User.class);
        when(user.getPartner()).thenReturn(partner);
        when(user.getUserProducts()).thenReturn(userProducts);
        when(user.getOperationalGroups()).thenReturn(operationalGroups);

        List<String> authorities = SecurityUtil.getUsersPermissionsCodeKeys(user);
        assertThat(authorities.size(), is(2));
    }
}
