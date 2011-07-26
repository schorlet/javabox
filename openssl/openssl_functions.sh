OPENSSL_CONF=$(dirname $0)/openssl.cnf
export OPENSSL_CONF

[ -z "$OPENSSL_DIR" ] && OPENSSL_DIR=/tmp/ca-washingmachine
[ -z "$OPENSSL_CRL_DP" ] && OPENSSL_CRL_DP=URI:file://$OPENSSL_DIR/crl.der
[ -z "$OPENSSL_OCSP" ] && OPENSSL_OCSP=
export OPENSSL_DIR
export OPENSSL_CRL_DP
export OPENSSL_OCSP

# --------------------------------------------------------------
# global
function _check_ca() {
    [ ! -r $OPENSSL_DIR/private/cacert.key ] && echo "0 -eq 1" && return;
    [ ! -r $OPENSSL_DIR/cacert.pem ] && echo "0 -eq 1" && return;
    [ ! -r $OPENSSL_DIR/cacert.crl ] && echo "0 -eq 1" && return;
    echo "0 -eq 0"
}
function setup() {
    [ -d $OPENSSL_DIR ] && return 0

    mkdir $OPENSSL_DIR
    mkdir $OPENSSL_DIR/newcerts
    mkdir $OPENSSL_DIR/private

    touch $OPENSSL_DIR/index.txt
    echo "2021" > $OPENSSL_DIR/serial
    chmod 700 $OPENSSL_DIR
    return 0
}

# --------------------------------------------------------------
# ca generation
CA_PASSWORD="hello toto"

function gen_ca() {
    [ $(_check_ca) ] && return 1

    openssl req $VERBOSE \
    -passout pass:"$CA_PASSWORD" \
    -new \
    -x509 \
    -days 3652 \
    -newkey rsa:1024 \
    -keyout $OPENSSL_DIR/private/cacert.key \
    -out $OPENSSL_DIR/cacert.pem \
    -subj '/CN=cacert/O=washingmachine/ST=france/C=FR'

    test $? != 0 && return 1

    _gencrl
    test $? != 0 && return 1

    _crl_pem2der
    test $? != 0 && return 1

    return 0
}

# --------------------------------------------------------------
# selfsigned server cert generation

function gen_server_crt_self() {
    [ -z "$1" ] && return 1
    local CN="$1"

    openssl req $VERBOSE \
    -new \
    -x509 \
    -extensions v3_server \
    -newkey rsa:1024 \
    -keyout /tmp/${CN}.key \
    -passout pass:$CN \
    -out /tmp/${CN}.pem \
    -subj "/CN=${CN}/O=washingmachine/ST=france/C=FR"

    test $? != 0 && return 1

    openssl pkcs12 \
    -export \
    -passin pass:$CN \
    -passout pass:$CN \
    -inkey /tmp/${CN}.key \
    -in /tmp/${CN}.pem \
    -name $CN \
    -out /tmp/${CN}.p12

    rm -f /tmp/${CN}.jks

    $JAVA_HOME/bin/keytool -importkeystore -v \
    -srckeystore /tmp/${CN}.p12 -destkeystore /tmp/${CN}.jks \
    -srcstoretype PKCS12 -deststoretype JKS \
    -srcstorepass $CN -deststorepass $CN \
    -srcalias $CN -destalias $CN \
    -srckeypass $CN -destkeypass $CN \
    -noprompt

    test $? != 0 && return 1

    return 0
}

# --------------------------------------------------------------
# server cert generation

function gen_server_crt() {
    [ ! $(_check_ca) ] && return 1
    [ -z "$1" ] && return 1
    local CN="$1"
    local EXTENSION="v3_server"
    [ ! -z "$2" ] && EXTENSION="$2"

    _gen_server_csr $CN
    test $? != 0 && return 1

    _sign_server_csr $CN $EXTENSION
    test $? != 0 && return 1

    _cert_text2pem $CN
    test $? != 0 && return 1

    _pkcs_client_cert $CN
    test $? != 0 && return 1

    return 0
}
function _gen_server_csr() {
    [ -z "$1" ] && return 1
    local CN="$1"

    # -nodes = la clé n'est pas protégée par un mot de passe
    openssl req $VERBOSE \
    -new \
    -newkey rsa:1024 \
    -nodes \
    -keyout /tmp/${CN}.key \
    -out /tmp/${CN}.csr \
    -subj "/CN=${CN}/O=washingmachine/ST=france/C=FR"

    test $? != 0 && return 1
    return 0
}
function _sign_server_csr() {
    [ -z "$1" ] && return 1
    [ -z "$2" ] && return 1
    local CN="$1"
    local EXTENSION="$2"

    openssl ca $VERBOSE \
    -passin pass:"$CA_PASSWORD" \
    -batch \
    -extensions $EXTENSION \
    -out /tmp/${CN}.pem \
    -in /tmp/${CN}.csr

    test $? != 0 && return 1

    local PURPOSE="sslserver"
    [ "$EXTENSION" = "v3_timestamping" ] && PURPOSE="timestampsign"
    [ "$EXTENSION" = "v3_ocsp" ] && PURPOSE="ocsphelper"

    openssl verify $VERBOSE \
    -purpose $PURPOSE \
    -CAfile $OPENSSL_DIR/cacert.pem \
    /tmp/${CN}.pem

    test $? != 0 && return 1

    if [ "$VERBOSE" = "-verbose" ]; then
        openssl x509 \
        -in /tmp/${CN}.pem -noout -text

        test $? != 0 && return 1
    fi

    return 0
}

# --------------------------------------------------------------
# client cert generation

function gen_client_crt() {
    [ ! $(_check_ca) ] && return 1
    [ -z "$1" ] && return 1
    [ -z "$2" ] && return 1
    [ ! \( "$2" = "ssl" -o "$2" = "signing" -o "$2" = "encipherment" \) ] && echo "unknown extension" >&2 && return 1;

    local CN="$1"
    local EXTENSION="v3_client_$2"
    local EMAIL=${CN}@washingmachine.org
    [ ! -z "$3" ] && EMAIL="$3"

    _gen_client_csr $CN $EMAIL
    test $? != 0 && return 1

    _sign_client_csr $CN $EXTENSION
    test $? != 0 && return 1

    _cert_text2pem $CN
    test $? != 0 && return 1

    _pkcs_client_cert $CN
    test $? != 0 && return 1

    return 0
}

function _gen_client_csr() {
    [ -z "$1" ] && return 1
    [ -z "$2" ] && return 1
    local CN="$1"
    local EMAIL="$2"

    _gen_rsa_key $CN $CN
    test $? != 0 && return 1

    openssl req $VERBOSE \
    -new \
    -key /tmp/${CN}.key \
    -passin pass:$CN \
    -out /tmp/${CN}.csr \
    -subj "/emailAddress=${EMAIL}/CN=${CN}/O=washingmachine/ST=france/C=FR"

    test $? != 0 && return 1
    return 0
}
function _sign_client_csr() {
    [ -z "$1" ] && return 1
    [ -z "$2" ] && return 1
    local CN="$1"
    local EXTENSION="$2"

    openssl ca $VERBOSE \
    -passin pass:"$CA_PASSWORD" \
    -batch \
    -policy policy_client \
    -extensions $EXTENSION \
    -out /tmp/${CN}.pem \
    -in /tmp/${CN}.csr

    test $? != 0 && return 1

    local PURPOSE="sslclient"
    [ "$EXTENSION" = "v3_client_signing" ] && PURPOSE="smimesign"
    [ "$EXTENSION" = "v3_client_encipherment" ] && PURPOSE="smimeencrypt"

    openssl verify $VERBOSE \
    -purpose $PURPOSE \
    -CAfile $OPENSSL_DIR/cacert.pem \
    /tmp/${CN}.pem

    test $? != 0 && return 1

    if [ "$VERBOSE" = "-verbose" ]; then
        openssl x509 \
        -in /tmp/${CN}.pem -noout -text

        test $? != 0 && return 1
    fi

    return 0
}
function _pkcs_client_cert() {
    [ -z "$1" ] && return 1
    local CN="$1"

    openssl pkcs12 \
    -export \
    -passin pass:$CN \
    -passout pass:$CN \
    -CAfile $OPENSSL_DIR/cacert.pem \
    -caname washingmachine-cacert \
    -inkey /tmp/${CN}.key \
    -in /tmp/${CN}.pem \
    -name $CN \
    -chain \
    -out /tmp/${CN}.p12

    test $? != 0 && return 1
    return 0
}
function _pkcs_ca() {
    username="`openssl x509 -noout -in $OPENSSL_DIR/cacert.pem -subject | sed -e 's;.*CN=;;' -e 's;/.*;;'`"

    openssl pkcs12 \
    -export \
    -passin pass:"$CA_PASSWORD" \
    -passout pass:"$username" \
    -in "$OPENSSL_DIR/cacert.pem" \
    -inkey "$OPENSSL_DIR/private/cacert.key" \
    -name "$username" \
    -out "$OPENSSL_DIR/cacert.p12"

    test $? != 0 && return 1

    if [ "$VERBOSE" = "-verbose" ]; then
        openssl pkcs12 -in "$OPENSSL_DIR/cacert.p12" -passin pass:"$username" -info -noout
        test $? != 0 && return 1
    fi

    return 0
}

# --------------------------------------------------------------
# generic operation

function _gen_rsa_key() {
    [ -z "$1" ] && return 1
    local CN="$1"

    local PASSWD_KEY=""
    local PASSWD_PUB=""
    [ ! -z "$2" ] && PASSWD_KEY="-des3 -passout pass:$2"
    [ ! -z "$2" ] && PASSWD_PUB="-passin pass:$2"

    openssl genrsa -out /tmp/${CN}.key $PASSWD_KEY 1024
    test $? != 0 && return 1

    openssl rsa -in /tmp/${CN}.key -pubout -out /tmp/${CN}.pub $PASSWD_PUB
    test $? != 0 && return 1

    return 0
}
function cert_serial() {
    [ ! $(_check_ca) ] && return 1
    [ -z "$1" ] && return 1
    local CN="$1"

    local SERIAL=$(openssl x509 -noout -in /tmp/${CN}.pem -serial)
    echo "${SERIAL/serial=/}"
}
function cert_email() {
    [ ! $(_check_ca) ] && return 1
    [ -z "$1" ] && return 1
    local CN="$1"

    local EMAIL=$(openssl x509 -noout -in /tmp/${CN}.pem -email)
    echo "${EMAIL}"
}
function _cert_text2pem() {
    [ -z "$1" ] && return 1
    local CN="$1"

    openssl x509 \
    -in /tmp/${CN}.pem \
    -out /tmp/${CN}.crt -outform PEM

    test $? != 0 && return 1

    openssl verify $VERBOSE \
    -CAfile $OPENSSL_DIR/cacert.pem \
    /tmp/${CN}.crt

    test $? != 0 && return 1

    if [ "$VERBOSE" = "-verbose" ]; then
        openssl x509 \
        -in /tmp/${CN}.crt -noout -text

        test $? != 0 && return 1
    fi

    return 0
}
function cert_is_invalid() {
    [ ! $(_check_ca) ] && return 1
    [ -z "$1" ] && return 1
    local CN="$1"

    openssl ca -status $(cert_serial $CN)
    local MESSAGE=$(openssl verify -CAfile $OPENSSL_DIR/cacert.crl -crl_check /tmp/${CN}.pem)

    local errfound=$(echo "$MESSAGE" | grep error | wc -l)
    test $errfound != 0 && return 1
    return 0
}
# --------------------------------------------------------------
# crl generation

function _gencrl() {
    openssl ca $VERBOSE \
    -passin pass:"$CA_PASSWORD" \
    -gencrl -crlexts crl_ext -out $OPENSSL_DIR/crl.pem

    test $? != 0 && return 1

    if [ "$VERBOSE" = "-verbose" ]; then
        openssl crl \
        -in $OPENSSL_DIR/crl.pem -text -noout

        test $? != 0 && return 1
    fi

    cat $OPENSSL_DIR/cacert.pem $OPENSSL_DIR/crl.pem > $OPENSSL_DIR/cacert.crl
    return 0
}
function _crl_pem2der() {
    openssl crl \
    -in $OPENSSL_DIR/crl.pem \
    -out $OPENSSL_DIR/crl.der \
    -outform DER

    test $? != 0 && return 1
    return 0
}

function revoke_cert() {
    [ ! $(_check_ca) ] && return 1
    [ -z "$1" ] && return 1
    local SERIAL=$(cert_serial $1)

    openssl ca $VERBOSE \
    -passin pass:"$CA_PASSWORD" \
    -revoke  $OPENSSL_DIR/newcerts/$SERIAL.pem

    test $? != 0 && return 1

    _gencrl
    test $? != 0 && return 1

    _crl_pem2der
    test $? != 0 && return 1

    return 0
}

# --------------------------------------------------------------
# encode, decode

ENC_PASSWORD="hello toto"
function encode_des3() {
    [ -z "$1" ] && return 1
    local FILE="$1"
    local FILE_OUT=$(tempfile)

    openssl enc -salt -des3 -base64 -in "$1" -out "$FILE_OUT" -pass pass:"$ENC_PASSWORD"
    test $? != 0 && return 1

    mv "$FILE_OUT" "$FILE"
}
function encode_aes192() {
    [ -z "$1" ] && return 1
    local FILE="$1"
    local FILE_OUT=$(tempfile)

    openssl enc -salt -aes-192-cbc -base64 -in "$FILE" -out "$FILE_OUT" -pass pass:"$ENC_PASSWORD"
    test $? != 0 && return 1

    mv "$FILE_OUT" "$FILE"
}
function encode_rsa() {
    [ -z "$1" ] && return 1
    [ -z "$2" ] && return 1
    local FILE="$1"
    local CN="$2"
    local FILE_OUT=$(tempfile)

    openssl rsautl -encrypt -in $FILE -out "$FILE_OUT" -inkey "/tmp/${CN}.pub" -pubin
    test $? != 0 && return 1

    mv "$FILE_OUT" "$FILE"
}

function decode_des3() {
    [ -z "$1" ] && return 1
    local FILE="$1"
    local FILE_OUT=$(tempfile)

    openssl enc -d -des3 -base64 -in "$FILE" -out "$FILE_OUT" -pass pass:"$ENC_PASSWORD"
    test $? != 0 && return 1

    mv "$FILE_OUT" "$FILE"
}
function decode_aes192() {
    [ -z "$1" ] && return 1
    local FILE="$1"
    local FILE_OUT=$(tempfile)

    openssl enc -d -aes-192-cbc -base64 -in "$FILE" -out "$FILE_OUT" -pass pass:"$ENC_PASSWORD"
    test $? != 0 && return 1

    mv "$FILE_OUT" "$FILE"
}
function decode_rsa() {
    [ -z "$1" ] && return 1
    [ -z "$2" ] && return 1
    local FILE="$1"
    local CN="$2"
    local FILE_OUT=$(tempfile)

    openssl rsautl -decrypt -in $FILE -out "$FILE_OUT" -inkey "/tmp/${CN}.key" -passin pass:$CN
    test $? != 0 && return 1

    mv "$FILE_OUT" "$FILE"
}

# --------------------------------------------------------------
# smime

function smime_sign() {
    [ ! $(_check_ca) ] && return 1
    [ -z "$1" ] && return 1
    [ -z "$2" ] && return 1
    [ -z "$3" ] && return 1
    local FROM="$1"
    local TO="$2"
    local FILE="$3"

    local EMAIL_FROM=$(cert_email $FROM)
    local EMAIL_TO=$(cert_email $TO)

    openssl smime -sign -in $FILE -out ${FILE}.signed \
    -from $EMAIL_FROM -to $EMAIL_TO -subject "Signed message" \
    -signer /tmp/${FROM}.crt -inkey /tmp/${FROM}.key -passin pass:$FROM

    test $? != 0 && return 1
    return 0
}
function smime_verify() {
    [ ! $(_check_ca) ] && return 1
    [ -z "$1" ] && return 1
    [ -z "$2" ] && return 1
    local FROM="$1"
    local FILE="$2"

    openssl smime -verify -signer /tmp/${FROM}.crt -in $FILE -out ${FILE}.verified \
    -crl_check_all -CAfile $OPENSSL_DIR/cacert.crl

    test $? != 0 && return 1
    return 0
}
function smime_encrypt() {
    [ ! $(_check_ca) ] && return 1
    [ -z "$1" ] && return 1
    [ -z "$2" ] && return 1
    [ -z "$3" ] && return 1
    local FROM="$1"
    local TO="$2"
    local FILE="$3"

    local EMAIL_FROM=$(cert_email $FROM)
    local EMAIL_TO=$(cert_email $TO)

    openssl smime -encrypt -in $FILE -out ${FILE}.encrypted \
    -from $EMAIL_FROM -to $EMAIL_TO -subject "Encrypted message" \
    -des3 /tmp/${TO}.crt

    test $? != 0 && return 1
    return 0
}
function smime_decrypt() {
    [ ! $(_check_ca) ] && return 1
    [ -z "$1" ] && return 1
    [ -z "$2" ] && return 1
    local TO="$1"
    local FILE="$2"

    openssl smime -decrypt -recip /tmp/${TO}.crt \
    -inkey /tmp/${TO}.key -passin pass:$TO \
    -in $FILE -out ${FILE}.decrypted

    test $? != 0 && return 1
    return 0
}
function smime_sign_and_encrypt() {
    [ ! $(_check_ca) ] && return 1
    [ -z "$1" ] && return 1
    [ -z "$2" ] && return 1
    [ -z "$3" ] && return 1
    local FROM="$1"
    local TO="$2"
    local FILE="$3"

    local EMAIL_FROM=$(cert_email $FROM)
    local EMAIL_TO=$(cert_email $TO)

    openssl smime -sign -in $FILE \
    -signer /tmp/${FROM}.crt -inkey /tmp/${FROM}.key -passin pass:$FROM |
    openssl smime -encrypt -out ${FILE}.signed_and_encrypted \
    -from $EMAIL_FROM -to $EMAIL_TO -subject "Signed and Encrypted message" \
    -des3 /tmp/${TO}.crt

    test $? != 0 && return 1
    return 0
}
function smime_decrypt_and_verify() {
    [ ! $(_check_ca) ] && return 1
    [ -z "$1" ] && return 1
    [ -z "$2" ] && return 1
    [ -z "$3" ] && return 1
    local FROM="$1"
    local TO="$2"
    local FILE="$3"

    openssl smime -decrypt -recip /tmp/${TO}.crt \
    -inkey /tmp/${TO}.key -passin pass:$TO \
    -in $FILE |
    openssl smime -verify -signer /tmp/${FROM}.crt \
    -crl_check_all -CAfile $OPENSSL_DIR/cacert.crl \
    -out ${FILE}.decrypted_and_verified

    test $? != 0 && return 1
    return 0
}

# --------------------------------------------------------------
# ts

# installation:
# wget http://www.opentsa.org/ts/ts-20060923-0_9_8c-patch.gz
# wget http://www.openssl.org/source/openssl-0.9.8c.tar.gz
# tar xzf openssl-0.9.8c.tar.gz
# cd openssl-0.9.8c
# gzip -cd ../ts-20060923-0_9_8c-patch.gz | patch -p1
# ./config
# make
# make test
#
# openssl version
# OpenSSL 0.9.8e 23 Feb 2007
#ldd /usr/bin/openssl
#       libssl.so.0.9.8 => /usr/lib/libssl.so.0.9.8 (0x00002b9d2694b000)
#       libcrypto.so.0.9.8 => /usr/lib/libcrypto.so.0.9.8 (0x00002b9d26b94000)
#       libdl.so.2 => /lib/libdl.so.2 (0x00002b9d26f15000)
#       libz.so.1 => /usr/lib/libz.so.1 (0x00002b9d27119000)
#       libc.so.6 => /lib/libc.so.6 (0x00002b9d27331000)
#       /lib64/ld-linux-x86-64.so.2 (0x00002b9d2672d000)
#
# sudo mv /usr/bin/openssl /usr/bin/openssl-0.9.8e
# sudo ln -s /home/sch/tmp/opentsa/openssl-0.9.8c/apps/openssl /usr/bin/openssl
#
# openssl version
# OpenSSL 0.9.8c 05 Sep 2006
#ldd /usr/bin/openssl
#       libdl.so.2 => /lib/libdl.so.2 (0x00002b58e9a07000)
#       libc.so.6 => /lib/libc.so.6 (0x00002b58e9c0b000)
#       /lib64/ld-linux-x86-64.so.2 (0x00002b58e97e9000)

function _check_tsa() {
    [ ! -r /tmp/tsacert.key ] && echo "0 -eq 1" && return;
    [ ! -r /tmp/tsacert.pem ] && echo "0 -eq 1" && return;
    cert_is_invalid tsacert
    if [ $? -ne 0 ]; then
        echo -e "tsacert is INVALID" >&2
        echo "0 -eq 1"
        return
    fi
    echo "0 -eq 0"
}
function gen_tsa() {
    [ $(_check_tsa) ] && return 1
    local CN="tsacert"
    local EXTENSION="v3_timestamping"

    gen_server_crt $CN $EXTENSION
    test $? != 0 && return 1

    echo "2020" > /tmp/tsaserial
    return 0
}

function ts_query() {
    [ -z "$1" ] && return 1
    local FILE="$1"

    openssl ts -query -cert -data $FILE -out $FILE.tsq
    test $? != 0 && return 1

    if [ "$VERBOSE" = "-verbose" ]; then
        openssl ts -query -in $FILE.tsq -text
        test $? != 0 && return 1
    fi
    return 0
}
function ts_reply() {
    [ ! $(_check_tsa) ] && return 1
    [ -z "$1" ] && return 1
    local FILE="$1"

    openssl ts -reply -queryfile $FILE \
    -passin pass:tsacert \
    -out $FILE.tsr

    test $? != 0 && return 1

    if [ "$VERBOSE" = "-verbose" ]; then
        openssl ts -reply -in $FILE.tsr -text
        test $? != 0 && return 1
    fi

    openssl ts -verify -queryfile $FILE -in $FILE.tsr \
    -CAfile $OPENSSL_DIR/cacert.pem

    test $? != 0 && return 1
    return 0
}
function ts_verify() {
    [ -z "$1" ] && return 1
    [ -z "$2" ] && return 1
    local ORIGINAL="$1"
    local REPLY="$2"

    openssl ts -verify -data $ORIGINAL -in $REPLY \
    -CAfile $OPENSSL_DIR/cacert.pem

    test $? != 0 && return 1
    return 0
}

# --------------------------------------------------------------
# ocsp

function _check_ocsp() {
    [ ! -r /tmp/ocsp.key ] && echo "0 -eq 1" && return;
    [ ! -r /tmp/ocsp.pem ] && echo "0 -eq 1" && return;
    cert_is_invalid ocsp
    if [ $? -ne 0 ]; then
        echo -e "ocsp is INVALID" >&2
        echo "0 -eq 1"
        return
    fi
    echo "0 -eq 0"
}

function gen_ocsp() {
    [ $(_check_ocsp) ] && return 1
    local CN="ocsp"
    local EXTENSION="v3_ocsp"

    gen_server_crt $CN $EXTENSION
    test $? != 0 && return 1
    return 0
}
function ocsp_server() {
    [ ! $(_check_ca) ] && return 1
    [ ! $(_check_ocsp) ] && return 1

    openssl ocsp -index $OPENSSL_DIR/index.txt -port 8888 \
    -CA $OPENSSL_DIR/cacert.pem \
    -rsigner /tmp/ocsp.pem -rkey /tmp/ocsp.key \
    -req_text

    return 0
}
function ocsp_client() {
    [ ! $(_check_ca) ] && return 1
    [ ! $(_check_ocsp) ] && return 1
    [ -z "$1" ] && return 1
    local CN="$1"

    openssl ocsp -issuer $OPENSSL_DIR/cacert.pem \
    -VAfile /tmp/ocsp.pem \
    -cert /tmp/${CN}.crt \
    -url http://localhost:8888 -resp_text

    return 0
}



