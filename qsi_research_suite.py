import numpy as np
import scipy.linalg as la

class QuranicSpectralIntelligence:
    """
    QSI (Quranic Spectral Intelligence) Research Suite.
    Implements advanced linear algebra, spectral theory, graph neural networks,
    and number-theoretic features for 114 Quranic Surah Matrices.
    """

    def __init__(self, matrices: dict):
        """
        Initialize the research suite with a dictionary of 114 matrices.
        :param matrices: Dictionary mapping surah index (1 to 114) to 28x28 numpy array
        """
        self.matrices = {int(k): np.array(v, dtype=np.float64) for k, v in matrices.items()}
        self.n_surahs = 114
        self.dim = 28

    def extract_spectral_features(self, s: int) -> dict:
        """
        Extract scientific features from the matrix M_s.
        """
        M = self.matrices[s]
        
        # 1. Trace and Frobenius Norm
        trace = np.trace(M)
        f_norm = la.norm(M, 'fro')
        
        # 2. Sparsity
        sparsity = np.count_nonzero(M == 0) / M.size
        
        # 3. Rank
        rank = np.linalg.matrix_rank(M)
        
        # 4. Determinant (only valid since matrix is square)
        det = np.linalg.det(M) if M.shape[0] == M.shape[1] else 0.0
        
        # 5. Symmetric Eigenvalues of 0.5 * (M + M^T)
        M_sym = 0.5 * (M + M.T)
        eigenvalues = la.eigvalsh(M_sym)
        
        # 6. Spectral Entropy
        eigen_abs = np.abs(eigenvalues)
        eigen_sum = np.sum(eigen_abs)
        if eigen_sum > 1e-9:
            p = eigen_abs / eigen_sum
            # Shannon entropy of probability distribution p
            spectral_entropy = -np.sum(p * np.log(p + 1e-15))
        else:
            spectral_entropy = 0.0
            
        # 7. Condition Number
        cond_no = np.linalg.cond(M)
        
        return {
            "surah": s,
            "rank": rank,
            "trace": trace,
            "determinant": det,
            "sparsity": sparsity,
            "frobenius_norm": f_norm,
            "spectral_entropy": spectral_entropy,
            "condition_number": cond_no,
            "eigenvalues": eigenvalues.tolist()
        }

    def compute_similarity_matrix(self) -> np.ndarray:
        """
        Constructs the 114x114 Attention-like similarity matrix S.
        S(i, j) = Tr(M_i M_j^T) / (||M_i||_F * ||M_j||_F)
        """
        S = np.zeros((self.n_surahs, self.n_surahs))
        norms = {s: la.norm(self.matrices[s], 'fro') for s in self.matrices}
        
        for i in range(1, self.n_surahs + 1):
            for j in range(1, self.n_surahs + 1):
                denominator = norms[i] * norms[j]
                if denominator > 1e-9:
                    inner_prod = np.sum(self.matrices[i] * self.matrices[j])
                    S[i-1, j-1] = inner_prod / denominator
                else:
                    S[i-1, j-1] = 0.0
        return S

    def generate_low_dim_embedding(self, S: np.ndarray, method="laplacian", d=2) -> np.ndarray:
        """
        Embeds the 114 surahs into a low d-dimensional coordinate space.
        Supports method="laplacian" (Laplacian Eigenmaps) or method="pca"
        """
        if method == "pca":
            # Center the similarity matrix
            n = S.shape[0]
            H = np.eye(n) - np.ones((n, n)) / n
            S_centered = H.dot(S).dot(H)
            
            # SVD decomposition
            U, s, Vt = la.svd(S_centered)
            return U[:, :d] * np.sqrt(s[:d])
            
        elif method == "laplacian":
            # Degree matrix D
            D = np.diag(np.sum(S, axis=1))
            # Graph Laplacian L = D - S
            L = D - S
            
            # Generalized eigenvalue problem L y = lambda D y
            eigenvalues, eigenvectors = la.eigh(L, D)
            
            # Sorted eigenvalues and eigenvectors (skip the first one lambda=0)
            idx = np.argsort(eigenvalues)
            return eigenvectors[:, idx[1:d+1]]
        
        else:
            raise ValueError(f"Unknown dimensionality reduction method: {method}")

    def propagate_reasoning_chain(self, chain: list, x0: np.ndarray) -> tuple:
        """
        Executes an offline reasoning chain of matrices:
        x0 -> M_i -> x1 -> M_j -> x2 -> M_k -> x3
        """
        states = [x0]
        current_state = x0
        for s in chain:
            current_state = np.dot(self.matrices[s], current_state)
            states.append(current_state)
        return tuple(states)

    def optimize_learnable_coefficients(self, x0: np.ndarray, y_target: np.ndarray, learning_rate=0.001, epochs=100) -> np.ndarray:
        """
        Lightweight neural optimization to find weighting coefficients alpha_i for H = Sum alpha_i M_i.
        Minimizes || y_target - H * x0 ||_2^2
        """
        alpha = np.zeros(self.n_surahs)
        
        # Precompute operational vectors for each matrix
        Mx0 = np.zeros((self.n_surahs, self.dim))
        for s in range(1, self.n_surahs + 1):
            Mx0[s-1] = np.dot(self.matrices[s], x0)
            
        for epoch in range(epochs):
            # Predicted target y_hat = Sum alpha_s (M_s * x0)
            y_hat = np.dot(alpha, Mx0)
            error = y_hat - y_target
            loss = np.sum(error**2)
            
            # Gradient dL/d_alpha_s = 2 * sum( error * M_s * x0 )
            gradient = 2.0 * np.dot(Mx0, error)
            alpha -= learning_rate * gradient
            
            if epoch % 10 == 0:
                print(f"Epoch {epoch:03d} | Loss: {loss:.4f}")
                
        return alpha

    def number_theoretic_spectrum(self, N: int, P=1009) -> np.ndarray:
        """
        Constructs the 114D Quranic number theory spectral profile Phi(N).
        Uses stable modular power vector v(N) to prevent numeric overflow.
        v(N) = [1, N, N^2, ... , N^27]^T mod P
        Phi(N) = (||M_1 v(N)|| , ... , ||M_114 v(N)||)
        """
        v = np.zeros(self.dim)
        for i in range(self.dim):
            v[i] = pow(N, i, P)
            
        # Normalize vector coordinates
        norm_v = la.norm(v)
        if norm_v > 1e-9:
            v /= norm_v
            
        phi = np.zeros(self.n_surahs)
        for s in range(1, self.n_surahs + 1):
            z = np.dot(self.matrices[s], v)
            phi[s-1] = la.norm(z)
            
        return phi
