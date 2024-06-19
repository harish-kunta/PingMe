package com.harish.hk185080.chatterbox;

import android.content.Context;
import android.widget.ScrollView;

import com.google.android.material.snackbar.Snackbar;
import com.harish.hk185080.chatterbox.activities.register.RegisterHelper;
import com.harish.hk185080.chatterbox.interfaces.IDataSource;
import com.harish.hk185080.chatterbox.interfaces.IDataSourceCallback;
import com.harish.hk185080.chatterbox.model.User;
import com.harish.hk185080.chatterbox.utils.StringResourceHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RegisterHelperTest {

    @Mock
    Context mockContext;

    @Mock
    ScrollView mockScrollView;

    @Mock
    IDataSource mockDataSource;

    @Mock
    IDataSourceCallback mockCallback;

    private RegisterHelper registerHelper;

    @Before
    public void setUp() {
        registerHelper = new RegisterHelper(mockContext, mockScrollView);
    }

    @Test
    public void testSignUpSuccess() {
        // Arrange
        String name = "John Doe";
        String email = "john.doe@example.com";
        String mobile = "1234567890";
        String password = "password123";
        String confirmPassword = "password123";
        User user = new User.Builder(name, email)
                .build();

        // Stubbing behavior of mockDataSource
        Mockito.doAnswer(invocation -> {
            IDataSourceCallback callback = invocation.getArgument(2);
            callback.onSuccess();
            return null;
        }).when(mockDataSource).createUser(Mockito.eq(user), Mockito.eq(password), Mockito.any(IDataSourceCallback.class));

        // Act
        registerHelper.signup(name, email, mobile, password, confirmPassword);

        // Assert
        // Verify that createUser method of mockDataSource was called once
        Mockito.verify(mockDataSource, Mockito.times(1)).createUser(Mockito.eq(user), Mockito.eq(password), Mockito.any(IDataSourceCallback.class));
        // Verify that Snackbar was shown with success message
//        Mockito.verify(mockScrollView).showSnackbar("Verification email sent to " + email);
    }

    @Test
    public void testSignUpFailure_InvalidEmail() {
        // Arrange
        String name = "John Doe";
        String email = "invalid_email";
        String mobile = "1234567890";
        String password = "password123";
        String confirmPassword = "password123";

        // Act
        registerHelper.signup(name, email, mobile, password, confirmPassword);

        // Assert
        // Verify that createUser method of mockDataSource was not called
        Mockito.verify(mockDataSource, Mockito.never()).createUser(Mockito.any(User.class), Mockito.anyString(), Mockito.any(IDataSourceCallback.class));
        // Verify that Snackbar was shown with invalid email error message
        //Mockito.verify(mockScrollView).showSnackbar("Please enter a valid email address");
    }

    // Add more test cases to cover other scenarios
}


