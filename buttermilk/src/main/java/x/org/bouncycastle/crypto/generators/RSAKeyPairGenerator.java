package x.org.bouncycastle.crypto.generators;


import x.org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import x.org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import x.org.bouncycastle.crypto.KeyGenerationParameters;
import x.org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import x.org.bouncycastle.crypto.params.RSAKeyParameters;
import x.org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;

import com.cryptoregistry.rsa.RSAKeyContents;
import com.cryptoregistry.rsa.RSAKeyMetadata;

import java.math.BigInteger;

/**
 * an RSA key pair generator.
 */
public class RSAKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
	private static final BigInteger ONE = BigInteger.valueOf(1);
	
	// optional, these are passed on to our wrapper, don't use both
	
	private char [] password;
	private RSAKeyMetadata management;
	
	public RSAKeyPairGenerator() {
		super();
	}

	public RSAKeyPairGenerator(char[] password) {
		super();
		this.password = password;
	}
	
	public RSAKeyPairGenerator(RSAKeyMetadata management) {
		super();
		this.management = management;
	}

	private RSAKeyGenerationParameters param;

	public void init(KeyGenerationParameters param) {
		this.param = (RSAKeyGenerationParameters) param;
	}

	public AsymmetricCipherKeyPair generateKeyPair() {
		BigInteger p, q, n, d, e, pSub1, qSub1, phi;

		//
		// p and q values should have a length of half the strength in bits
		//
		int strength = param.getStrength();
		int pbitlength = (strength + 1) / 2;
		int qbitlength = strength - pbitlength;
		int mindiffbits = strength / 3;

		e = param.getPublicExponent();

		// TODO Consider generating safe primes for p, q (see
		// DHParametersHelper.generateSafePrimes)
		// (then p-1 and q-1 will not consist of only small factors - see
		// "Pollard's algorithm")

		//
		// generate p, prime and (p-1) relatively prime to e
		//
		for (;;) {
			p = new BigInteger(pbitlength, 1, param.getRandom());

			if (p.mod(e).equals(ONE)) {
				continue;
			}

			if (!p.isProbablePrime(param.getCertainty())) {
				continue;
			}

			if (e.gcd(p.subtract(ONE)).equals(ONE)) {
				break;
			}
		}

		//
		// generate a modulus of the required length
		//
		for (;;) {
			// generate q, prime and (q-1) relatively prime to e,
			// and not equal to p
			//
			for (;;) {
				q = new BigInteger(qbitlength, 1, param.getRandom());

				if (q.subtract(p).abs().bitLength() < mindiffbits) {
					continue;
				}

				if (q.mod(e).equals(ONE)) {
					continue;
				}

				if (!q.isProbablePrime(param.getCertainty())) {
					continue;
				}

				if (e.gcd(q.subtract(ONE)).equals(ONE)) {
					break;
				}
			}

			//
			// calculate the modulus
			//
			n = p.multiply(q);

			if (n.bitLength() == param.getStrength()) {
				break;
			}

			//
			// if we get here our primes aren't big enough, make the largest
			// of the two p and try again
			//
			p = p.max(q);
		}

		if (p.compareTo(q) < 0) {
			phi = p;
			p = q;
			q = phi;
		}

		pSub1 = p.subtract(ONE);
		qSub1 = q.subtract(ONE);
		phi = pSub1.multiply(qSub1);

		//
		// calculate the private exponent
		//
		d = e.modInverse(phi);

		//
		// calculate the CRT factors
		//
		BigInteger dP, dQ, qInv;

		dP = d.remainder(pSub1);
		dQ = d.remainder(qSub1);
		qInv = q.modInverse(p);

		return new AsymmetricCipherKeyPair(new RSAKeyParameters(false, n, e),
				new RSAPrivateCrtKeyParameters(n, e, d, p, q, dP, dQ, qInv));
	}
	
	public RSAKeyContents generateKeys() {
		BigInteger p, q, n, d, e, pSub1, qSub1, phi;

		//
		// p and q values should have a length of half the strength in bits
		//
		int strength = param.getStrength();
		int pbitlength = (strength + 1) / 2;
		int qbitlength = strength - pbitlength;
		int mindiffbits = strength / 3;

		e = param.getPublicExponent();

		// TODO Consider generating safe primes for p, q (see
		// DHParametersHelper.generateSafePrimes)
		// (then p-1 and q-1 will not consist of only small factors - see
		// "Pollard's algorithm")

		//
		// generate p, prime and (p-1) relatively prime to e
		//
		for (;;) {
			p = new BigInteger(pbitlength, 1, param.getRandom());

			if (p.mod(e).equals(ONE)) {
				continue;
			}

			if (!p.isProbablePrime(param.getCertainty())) {
				continue;
			}

			if (e.gcd(p.subtract(ONE)).equals(ONE)) {
				break;
			}
		}

		//
		// generate a modulus of the required length
		//
		for (;;) {
			// generate q, prime and (q-1) relatively prime to e,
			// and not equal to p
			//
			for (;;) {
				q = new BigInteger(qbitlength, 1, param.getRandom());

				if (q.subtract(p).abs().bitLength() < mindiffbits) {
					continue;
				}

				if (q.mod(e).equals(ONE)) {
					continue;
				}

				if (!q.isProbablePrime(param.getCertainty())) {
					continue;
				}

				if (e.gcd(q.subtract(ONE)).equals(ONE)) {
					break;
				}
			}

			//
			// calculate the modulus
			//
			n = p.multiply(q);

			if (n.bitLength() == param.getStrength()) {
				break;
			}

			//
			// if we get here our primes aren't big enough, make the largest
			// of the two p and try again
			//
			p = p.max(q);
		}

		if (p.compareTo(q) < 0) {
			phi = p;
			p = q;
			q = phi;
		}

		pSub1 = p.subtract(ONE);
		qSub1 = q.subtract(ONE);
		phi = pSub1.multiply(qSub1);

		//
		// calculate the private exponent
		//
		d = e.modInverse(phi);

		//
		// calculate the CRT factors
		//
		BigInteger dP, dQ, qInv;

		dP = d.remainder(pSub1);
		dQ = d.remainder(qSub1);
		qInv = q.modInverse(p);
		
		if(password != null){
			return new RSAKeyContents(password,n, e, d, p, q, dP, dQ, qInv);
		}else if(management != null){
			return new RSAKeyContents(management,n, e, d, p, q, dP, dQ, qInv);
		}else{
			return new RSAKeyContents(n, e, d, p, q, dP, dQ, qInv);
		}
	}
}
