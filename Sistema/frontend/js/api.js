/**
 * Camada de comunicação com a API REST do SGA-Edu.
 * Todas as chamadas passam por aqui — centraliza o token JWT e tratamento de erros.
 */

const API_BASE = 'http://localhost:8080/api/v1';

const Api = (() => {

  function token() {
    return localStorage.getItem('sgaedu_token');
  }

  function cabecalhos(comCorpo = true) {
    const h = { Authorization: `Bearer ${token()}` };
    if (comCorpo) h['Content-Type'] = 'application/json';
    return h;
  }

  async function tratar(response) {
    if (response.status === 204) return null;
    const corpo = await response.json().catch(() => ({}));
    if (!response.ok) {
      const msg = corpo.message || corpo.detail || `Erro ${response.status}`;
      throw new Error(msg);
    }
    return corpo;
  }

  return {
    // --- Auth ---
    async login(email, senha) {
      const res = await fetch(`${API_BASE}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, senha }),
      });
      return tratar(res);
    },

    // --- Notas ---
    async lancarNota(turmaId, alunoId, avaliacaoId, valor, observacao) {
      const res = await fetch(`${API_BASE}/turmas/${turmaId}/notas`, {
        method: 'POST',
        headers: cabecalhos(),
        body: JSON.stringify({ alunoId, avaliacaoId, valor, observacao }),
      });
      return tratar(res);
    },

    async listarNotas(turmaId, alunoId) {
      const res = await fetch(`${API_BASE}/turmas/${turmaId}/notas?alunoId=${alunoId}`, {
        headers: cabecalhos(false),
      });
      return tratar(res);
    },

    async listarNotasDaTurma(turmaId) {
      const res = await fetch(`${API_BASE}/turmas/${turmaId}/notas`, {
        headers: cabecalhos(false),
      });
      return tratar(res);
    },

    // --- Matrículas ---
    async matricular(alunoId, turmaId) {
      const res = await fetch(`${API_BASE}/matriculas`, {
        method: 'POST',
        headers: cabecalhos(),
        body: JSON.stringify({ alunoId, turmaId }),
      });
      return tratar(res);
    },

    async cancelarMatricula(matriculaId, motivo) {
      const res = await fetch(`${API_BASE}/matriculas/${matriculaId}?motivo=${encodeURIComponent(motivo)}`, {
        method: 'DELETE',
        headers: cabecalhos(false),
      });
      return tratar(res);
    },

    async listarMatriculas(alunoId) {
      const res = await fetch(`${API_BASE}/matriculas?alunoId=${alunoId}`, {
        headers: cabecalhos(false),
      });
      return tratar(res);
    },

    async listarTodasMatriculas() {
      const res = await fetch(`${API_BASE}/matriculas/todas`, {
        headers: cabecalhos(false),
      });
      return tratar(res);
    },

    async listarAlunos() {
      const res = await fetch(`${API_BASE}/usuarios/alunos`, {
        headers: cabecalhos(false),
      });
      return tratar(res);
    },

    async cadastrarAluno(dados) {
      const res = await fetch(`${API_BASE}/usuarios/alunos`, {
        method: 'POST',
        headers: cabecalhos(),
        body: JSON.stringify(dados),
      });
      return tratar(res);
    },

    async obterAluno(alunoId) {
      const res = await fetch(`${API_BASE}/usuarios/alunos/${alunoId}`, {
        headers: cabecalhos(false),
      });
      return tratar(res);
    },

    async atualizarAluno(alunoId, dados) {
      const res = await fetch(`${API_BASE}/usuarios/alunos/${alunoId}`, {
        method: 'PUT',
        headers: cabecalhos(),
        body: JSON.stringify(dados),
      });
      return tratar(res);
    },

    async listarTurmas() {
      const res = await fetch(`${API_BASE}/turmas`, {
        headers: cabecalhos(false),
      });
      return tratar(res);
    },

    async listarAlunosDaTurma(turmaId) {
      const res = await fetch(`${API_BASE}/turmas/${turmaId}/alunos`, {
        headers: cabecalhos(false),
      });
      return tratar(res);
    },

    async cadastrarTurma(dados) {
      const res = await fetch(`${API_BASE}/turmas`, {
        method: 'POST',
        headers: cabecalhos(),
        body: JSON.stringify(dados),
      });
      return tratar(res);
    },

    async listarAvaliacoes(turmaId) {
      const res = await fetch(`${API_BASE}/turmas/${turmaId}/avaliacoes`, {
        headers: cabecalhos(false),
      });
      return tratar(res);
    },

    async cadastrarAvaliacao(turmaId, dados) {
      const res = await fetch(`${API_BASE}/turmas/${turmaId}/avaliacoes`, {
        method: 'POST',
        headers: cabecalhos(),
        body: JSON.stringify(dados),
      });
      return tratar(res);
    },

    // --- Frequência ---
    async registrarFrequencia(turmaId, alunoId, data, presente, justificativa) {
      const res = await fetch(`${API_BASE}/turmas/${turmaId}/frequencias`, {
        method: 'POST',
        headers: cabecalhos(),
        body: JSON.stringify({ alunoId, data, presente, justificativa }),
      });
      return tratar(res);
    },

    async consultarFrequencia(turmaId, alunoId) {
      const res = await fetch(`${API_BASE}/turmas/${turmaId}/frequencias/resumo?alunoId=${alunoId}`, {
        headers: cabecalhos(false),
      });
      return tratar(res);
    },

    // Retorna o ID do usuário logado (extraído do JWT)
    async obterMeuId() {
      const res = await fetch(`${API_BASE}/usuarios/me`, {
        headers: cabecalhos(false),
      });
      const dados = await tratar(res);
      return dados.id;
    },

    // --- Documentos ---
    async gerarBoletim(alunoId, turmaId, periodo) {
      const res = await fetch(
        `${API_BASE}/documentos/boletim?alunoId=${alunoId}&turmaId=${turmaId}&periodo=${encodeURIComponent(periodo)}`,
        { headers: cabecalhos(false) }
      );
      if (!res.ok) {
        const msg = await res.json().catch(() => ({}));
        throw new Error(msg.message || `Erro ${res.status}`);
      }
      return res.blob();
    },

    async gerarHistorico(alunoId) {
      const res = await fetch(`${API_BASE}/documentos/historico?alunoId=${alunoId}`, {
        headers: cabecalhos(false),
      });
      if (!res.ok) {
        const msg = await res.json().catch(() => ({}));
        throw new Error(msg.message || `Erro ${res.status}`);
      }
      return res.blob();
    },
  };
})();
