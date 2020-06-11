#!/bin/bash

#   Output files
# ca.key: Certificate Authority private key file (PRIVATE DATA)
# ca.crt: Certificate Authority trust certificate (FILE TO SHARE)

# server.key: Server private key, password protected (PRIVATE DATA)
# server.csr: Server certificate signing request (SHARING WITH CA OWNER)
# server.crt: Server certificate signed by CA
# server.pem: Conversion of server.key into a format gRPC likes (PRIVATE DATA)

#      P R I V A T E   F I L E S
# ca.key  server.key  server.pem  server.crt

#      S H A R E   F I L E S
# ca.crt (CLIENT)   server.csr (CA)

# Changes this CN's to match your hosts in your env if needed
SERVER_CN=localhost

# [ 1]: Generate certificate authority + trust certificate(ca.crt)
openssl genrsa -passout pass:1111 -des3 -out ca.key 4096
openssl req -passin pass:1111 -new -x509 -days 365 -key ca.key -out ca.crt -subj "/CN=${SERVER_CN}"

# [ 2]: Generate the server private key - server.key
openssl genrsa -passout pass:1111 -des3 -out server.key 4096

# [ 3]: Get a signed sertificate from from the CA - server.csr
openssl req -passin pass:1111 -new -key server.key -out server.csr -subj "/CN=${SERVER_CN}"

# [ 4]: Sign the certificate with the CA we created (its called self-signing) - server.crt
openssl x509 -req -passin pass:1111 -days 365 -in server.csr -CA ca.crt -CAkey ca.key -set_serial 01 -out server.crt

# [ 5]: Convert the server certificate to .pem format (server.pem) - usable by gRPC
openssl pkcs8 -topk8 -nocrypt -passin pass:1111 -in server.key -out server.pem

