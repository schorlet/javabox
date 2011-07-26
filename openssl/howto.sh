#!/bin/bash

# configuration :
OPENSSL_DIR=/tmp/ca-washingmachine
#OPENSSL_CRL_DP=URI:file://$OPENSSL_DIR/crl.der
OPENSSL_CRL_DP=URI:http://localhost:8080/crl.der
OPENSSL_OCSP="OCSP;URI:http://localhost:8080"
VERBOSE=""
#VERBOSE="-verbose"

# CN
SERVER="localhost"
APACHE_CLIENT="apacheclient"
SIGNING_CLIENT="signingclient"
ENCIPHER_CLIENT="encipherclient"

if [ -z "$1" -o "$1" = "--help" ]; then
    echo "Certificate Commands:
    gen_ca
    gen_server <CN>
    gen_server_self <CN>
    gen_client <CN>
    gen_signing <CN> <EMAIL>
    gen_encipher <CN> <EMAIL>
    revoke <CN>
    verify <CN>

Encrypt and Decrypt Commands:
    encode_des3 <file>
    decode_des3 <file>
    encode_aes192 <file>
    decode_aes192 <file>
    encode_rsa <file> <encipher CN>
    decode_rsa <file> <encipher CN>

Email Commands:
    smime_sign <signing CN> <encipher CN> <file>
    smime_verify <signing CN> <file>

    smime_encrypt <signing CN> <encipher CN> <file>
    smime_decrypt <encipher CN> <file>

    smime_sign_and_encrypt <signing CN> <encipher CN> <file>
    smime_decrypt_and_verify <signing CN> <encipher CN> <file>

Timestamping Commands:
    gen_tsa
    ts_query <file>
    ts_reply <query file>
    ts_verify <file> <response file>

OCSP Commands:
    gen_oscp
    ocsp_server
    oscp_client <CN>
"
    exit 0

elif [ "$1" = "test" ]; then
    rm -rf $OPENSSL_DIR

    ($0 gen_ca)
    ($0 gen_server)
    ($0 gen_client)
    ($0 gen_signing)
    ($0 gen_encipher)

    echo "hello toto encode_des3" > /tmp/message
    ($0 encode_des3 /tmp/message)
    ($0 decode_des3 /tmp/message)
    cat /tmp/message
    echo "hello toto encode_aes192" > /tmp/message
    ($0 encode_aes192 /tmp/message)
    ($0 decode_aes192 /tmp/message)
    cat /tmp/message
    echo "hello toto encode_rsa" > /tmp/message
    ($0 encode_rsa /tmp/message $ENCIPHER_CLIENT)
    ($0 decode_rsa /tmp/message $ENCIPHER_CLIENT)
    cat /tmp/message

    echo "Signed message" > /tmp/message
    ($0 smime_sign $SIGNING_CLIENT $ENCIPHER_CLIENT /tmp/message)
    ($0 smime_verify $SIGNING_CLIENT /tmp/message.signed)
    cat /tmp/message.signed.verified
    echo "Encrypted message" > /tmp/message
    ($0 smime_encrypt $SIGNING_CLIENT $ENCIPHER_CLIENT /tmp/message)
    ($0 smime_decrypt $ENCIPHER_CLIENT /tmp/message.encrypted)
    cat /tmp/message.encrypted.decrypted
    echo "Signed and Encrypted message" > /tmp/message
    ($0 smime_sign_and_encrypt $SIGNING_CLIENT $ENCIPHER_CLIENT /tmp/message)
    ($0 smime_decrypt_and_verify $SIGNING_CLIENT $ENCIPHER_CLIENT /tmp/message.signed_and_encrypted)
    cat /tmp/message.signed_and_encrypted.decrypted_and_verified

    ($0 revoke $SERVER)
    ($0 revoke $APACHE_CLIENT)
    ($0 revoke $SIGNING_CLIENT)
    ($0 revoke $ENCIPHER_CLIENT)

    ($0 verify $SERVER)
    ($0 verify $APACHE_CLIENT)
    ($0 verify $SIGNING_CLIENT)
    ($0 verify $ENCIPHER_CLIENT)

    openssl ts --help 2>&1 | grep -i error
    if test $? -eq 1; then
    echo "ts message" > /tmp/message
    rm /tmp/tsacert.key /tmp/tsacert.csr /tmp/tsacert.pem /tmp/tsacert.crt
    ($0 gen_tsa)
    ($0 ts_query /tmp/message)
    ($0 ts_reply /tmp/message.tsq)
    ($0 ts_verify /tmp/message /tmp/message.tsq.tsr)
    ($0 revoke tsacert)
    ($0 verify tsacert)
    fi

    exit 0
fi

source $(dirname $0)/openssl_functions.sh

setup
test $? != 0 && exit 1

if [ "$1" = "gen_ca" ]; then
    gen_ca
    test $? != 0 && echo "ca already exists" >&2 && exit 1

elif [ "$1" = "gen_tsa" ]; then
    gen_tsa
    test $? != 0 && echo "tsa already exists" >&2 && exit 1

elif [ "$1" = "gen_ocsp" ]; then
    gen_ocsp
    test $? != 0 && echo "ocsp already exists" >&2 && exit 1

elif [ "$1" = "gen_server" ]; then
    [ ! -z $2 ] && SERVER=$2
    gen_server_crt $SERVER
    test $? != 0 && exit 1

elif [ "$1" = "gen_server_self" ]; then
    [ ! -z $2 ] && SERVER=$2
    gen_server_crt_self $SERVER
    test $? != 0 && exit 1

elif [ "$1" = "gen_client" ]; then
    [ ! -z $2 ] && APACHE_CLIENT=$2
    gen_client_crt $APACHE_CLIENT "ssl"
    test $? != 0 && exit 1

elif [ "$1" = "gen_signing" ]; then
    [ ! -z $2 ] && SIGNING_CLIENT=$2
    gen_client_crt $SIGNING_CLIENT "signing" $3
    test $? != 0 && exit 1

elif [ "$1" = "gen_encipher" ]; then
    [ ! -z $2 ] && ENCIPHER_CLIENT=$2
    gen_client_crt $ENCIPHER_CLIENT "encipherment" $3
    test $? != 0 && exit 1

elif [ "$1" = "revoke" -a ! -z "$2" ]; then
    revoke_cert $2
    test $? != 0 && exit 1

elif [ "$1" = "verify" -a ! -z "$2" ]; then
    cert_is_invalid $2
    test $? != 0 && echo "$2 is INVALID" || echo "$2 is OK"

elif [ "${1:0:7}" = "encode_" -o "${1:0:7}" = "decode_" ]; then
    # encode_des3, decode_des3, encode_aes192, decode_aes192
    # encode_rsa, decode_rsa
    ($1 $2 $3)
    test $? != 0 && exit 1

elif [ "${1:0:6}" = "smime_" ]; then
    # smime_sign, smime_verify
    # smime_encrypt, smime_decrypt
    # smime_sign_and_encrypt, smime_decrypt_and_verify
    ($1 $2 $3 $4)
    test $? != 0 && exit 1

elif [ "${1:0:3}" = "ts_" -a ! -z "$2" ]; then
    # ts_query, ts_reply, ts_verify
    ($1 $2 $3)
    test $? != 0 && exit 1

elif [ "${1:0:5}" = "ocsp_" ]; then
    # ocsp_server, ocsp_client
    ($1 $2)
    test $? != 0 && exit 1

else
    echo "error (unknown args): $@" >&2
    exit 1
fi

exit 0




