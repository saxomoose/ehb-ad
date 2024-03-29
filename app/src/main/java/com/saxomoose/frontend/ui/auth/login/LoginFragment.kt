package com.saxomoose.frontend.ui.auth.login

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.saxomoose.frontend.FrontEndApplication
import com.saxomoose.frontend.R
import com.saxomoose.frontend.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels {
        LoginViewModelFactory((activity?.application as FrontEndApplication).backendService)
    }
    private var successfulRegistration: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { successfulRegistration = it.getBoolean("successfulRegistration") }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        binding = FragmentLoginBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // If successful registration, invite user to activate account.
        if (successfulRegistration) {
            val fragmentManager = parentFragmentManager
            val dialogFragment = SuccessfulRegistrationDialogFragment()
            dialogFragment.show(fragmentManager, "successfulRegistrationDialog")
        }

        val username = binding.username
        val password = binding.password
        val registerButton = binding.loginButton
        val loading = binding.loginLoading

        binding.viewModel = viewModel

        viewModel.loginFormState.observe(viewLifecycleOwner, Observer {
            val loginState = it ?: return@Observer

            // Disable login button unless name, username and password are valid.
            registerButton.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        viewModel.loginResult.observe(viewLifecycleOwner, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (!loginResult) {
                showLoginFailed()
            }
            // If login succeeds, write token and userId to SharedPreferences.
            if (loginResult) {
                val sharedPref = activity?.getSharedPreferences(
                    getString(R.string.preference_file_key),
                    Context.MODE_PRIVATE
                )
                with(sharedPref?.edit()) {
                    this?.putString(getString(R.string.token), viewModel.token)
                    this?.putInt(getString(R.string.userId), viewModel.userId!!)
                    this?.apply()
                }
                val activity = activity as ActivityLauncher
                activity.launchMainActivity()
            }
        })

        username.afterTextChanged {
            viewModel.loginDataChanged(username.text.toString(), password.text.toString())
        }

        password.apply {
            afterTextChanged {
                viewModel.loginDataChanged(username.text.toString(), password.text.toString())
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> viewModel.login(
                        username.text.toString(),
                        password.text.toString()
                    )
                }
                false
            }

            registerButton.setOnClickListener {
                loading.visibility = View.VISIBLE
                viewModel.login(username.text.toString(), password.text.toString())
            }
        }
    }

    private fun showLoginFailed() {
        Toast.makeText(activity?.applicationContext, "Login failed", Toast.LENGTH_LONG).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        }
    })
}