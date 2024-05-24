package de.iks.rataplan.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Singular;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;

import java.util.Set;

@Data
@RequiredArgsConstructor
@Builder
public class OpenIDAutoDiscoveryDTO {
    private final String issuer;
    private final String authorization_endpoint;
    private final String token_endpoint;
    @Singular("token_endpoint_auth_method_supported") private final Set<String> token_endpoint_auth_methods_supported;
    @Singular("token_endpoint_auth_signing_alg_value_supported") private final Set<String> token_endpoint_auth_signing_alg_values_supported;
    private final String userinfo_endpoint;
    private final String check_session_iframe;
    private final String end_session_endpoint;
    private final String jwks_uri;
    private final String registration_endpoint;
    @Singular("scope_supported") private final Set<String> scopes_supported;
    @Singular("response_type_supported") private final Set<String> response_types_supported;
    @Singular("acr_value_supported") private final Set<String> acr_values_supported;
    @Singular("subject_type_supported") private final Set<String> subject_types_supported;
    @Singular("userinfo_signing_alg_value_supported") private final Set<JWSAlgorithm> userinfo_signing_alg_values_supported;
    @Singular("userinfo_encryption_alg_value_supported") private final Set<JWEAlgorithm> userinfo_encryption_alg_values_supported;
    @Singular("userinfo_encryption_enc_value_supported") private final Set<EncryptionMethod> userinfo_encryption_enc_values_supported;
    @Singular("id_token_signing_alg_value_supported") private final Set<JWSAlgorithm> id_token_signing_alg_values_supported;
    @Singular("id_token_encryption_alg_value_supported") private final Set<JWEAlgorithm> id_token_encryption_alg_values_supported;
    @Singular("id_token_encryption_enc_value_supported") private final Set<EncryptionMethod> id_token_encryption_enc_values_supported;
    @Singular("request_object_signing_alg_value_supported") private final Set<JWSAlgorithm> request_object_signing_alg_values_supported;
    @Singular("display_value_supported") private final Set<String> display_values_supported;
    @Singular("claim_type_supported") private final Set<String> claim_types_supported;
    @Singular("claim_supported") private final Set<String> claims_supported;
    private final Boolean claims_parameter_supported;
    private final String service_documentation;
    private final Set<String> ui_locales_supported;
}