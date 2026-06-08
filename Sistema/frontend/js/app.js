/**
 * SGA-Edu — SPA principal
 * Menus e cards seguem os documentos base (Etapa 1 e Etapa 2).
 */

// =====================================================================
// Estado global
// =====================================================================
const Estado = {
  token: null, email: null, papel: null, alunoId: null,
  alunos: [], turmas: [],

  salvar(token, email, papel) {
    this.token = token; this.email = email; this.papel = papel;
    localStorage.setItem('sgaedu_token', token);
    localStorage.setItem('sgaedu_email', email);
    localStorage.setItem('sgaedu_papel', papel);
  },

  restaurar() {
    this.token = localStorage.getItem('sgaedu_token');
    this.email = localStorage.getItem('sgaedu_email');
    this.papel = localStorage.getItem('sgaedu_papel');
    return !!this.token;
  },

  limpar() {
    this.token = null; this.email = null; this.papel = null;
    localStorage.clear();
  },
};

// =====================================================================
// Roteamento
// =====================================================================
function mostrarPagina(id) {
  document.querySelectorAll('.pagina').forEach(p => p.classList.remove('ativa'));
  document.querySelectorAll('.nav-link').forEach(l => l.classList.remove('ativo'));
  const p = document.getElementById(id);
  if (p) p.classList.add('ativa');
  const l = document.querySelector(`.nav-link[data-pagina="${id}"]`);
  if (l) l.classList.add('ativo');
}

// =====================================================================
// Menus por papel — conforme documentos base
// =====================================================================
const MENUS = {
  PROFESSOR: [
    { id: 'pagina-dashboard',   label: 'Início' },
    { id: 'pagina-notas',       label: 'Lançar Notas' },      // UC-02
    { id: 'pagina-frequencia',  label: 'Frequência' },         // UC-05
  ],
  ALUNO: [
    { id: 'pagina-dashboard',        label: 'Início' },
    { id: 'pagina-minhas-notas',     label: 'Minhas Notas' },
    { id: 'pagina-minha-frequencia', label: 'Minha Frequência' },
    { id: 'pagina-documentos',       label: 'Documentos' },    // UC-04
  ],
  SECRETARIA: [
    { id: 'pagina-dashboard',   label: 'Início' },
    { id: 'pagina-matriculas',  label: 'Matrículas' },         // UC-01
    { id: 'pagina-documentos',  label: 'Documentos' },         // UC-04
  ],
  DIRETOR: [
    { id: 'pagina-dashboard',        label: 'Início' },
    { id: 'pagina-matriculas',       label: 'Matrículas' },    // UC-01
    { id: 'pagina-notas',            label: 'Notas' },          // UC-02
    { id: 'pagina-frequencia',       label: 'Frequência' },     // UC-05
    { id: 'pagina-documentos',       label: 'Documentos' },     // UC-04
  ],
};

const CARDS = {
  PROFESSOR: [
    { icone: '📝', titulo: 'Lançar Notas',     descricao: 'Registrar notas das avaliações (UC-02)',  pagina: 'pagina-notas' },
    { icone: '📅', titulo: 'Registrar Freq.',  descricao: 'Registrar presença dos alunos (UC-05)',   pagina: 'pagina-frequencia' },
  ],
  ALUNO: [
    { icone: '📊', titulo: 'Minhas Notas',     descricao: 'Consultar notas das avaliações',          pagina: 'pagina-minhas-notas' },
    { icone: '📅', titulo: 'Minha Freq.',      descricao: 'Ver frequência e situação (RN-001)',       pagina: 'pagina-minha-frequencia' },
    { icone: '📄', titulo: 'Documentos',       descricao: 'Boletim e histórico escolar (UC-04)',      pagina: 'pagina-documentos' },
  ],
  SECRETARIA: [
    { icone: '📌', titulo: 'Matrículas',       descricao: 'Matricular e cancelar alunos (UC-01)',    pagina: 'pagina-matriculas' },
    { icone: '📄', titulo: 'Documentos',       descricao: 'Boletim e histórico escolar (UC-04)',      pagina: 'pagina-documentos' },
  ],
  DIRETOR: [
    { icone: '📌', titulo: 'Matrículas',       descricao: 'Matricular e cancelar alunos (UC-01)',    pagina: 'pagina-matriculas' },
    { icone: '📝', titulo: 'Notas',            descricao: 'Consultar notas das turmas (UC-02)',       pagina: 'pagina-notas' },
    { icone: '📅', titulo: 'Frequência',       descricao: 'Consultar frequência das turmas (UC-05)', pagina: 'pagina-frequencia' },
    { icone: '📄', titulo: 'Documentos',       descricao: 'Boletim e histórico escolar (UC-04)',      pagina: 'pagina-documentos' },
  ],
};

// =====================================================================
// Construção da navegação e dashboard
// =====================================================================
function construirNavegacao(papel) {
  const nav = document.getElementById('nav-links');
  nav.innerHTML = '';
  (MENUS[papel] || MENUS.ALUNO).forEach(item => {
    const btn = document.createElement('button');
    btn.className = 'nav-link';
    btn.dataset.pagina = item.id;
    btn.textContent = item.label;
    btn.addEventListener('click', () => mostrarPagina(item.id));
    nav.appendChild(btn);
  });
}

function construirDashboard(papel, email) {
  document.getElementById('dash-nome').textContent = email;
  const badge = document.getElementById('dash-papel');
  badge.textContent = papel;
  badge.className = `badge badge-${papel}`;

  const grid = document.getElementById('cards-dashboard');
  grid.innerHTML = '';
  (CARDS[papel] || []).forEach(c => {
    const div = document.createElement('div');
    div.className = 'card-dashboard';
    div.innerHTML = `<span class="icone">${c.icone}</span><h3>${c.titulo}</h3><p>${c.descricao}</p>`;
    div.addEventListener('click', () => mostrarPagina(c.pagina));
    grid.appendChild(div);
  });
}

// =====================================================================
// Login
// =====================================================================
document.getElementById('form-login').addEventListener('submit', async (e) => {
  e.preventDefault();
  const email = document.getElementById('email').value.trim();
  const senha = document.getElementById('senha').value;
  const errDiv = document.getElementById('erro-login');
  const btn    = document.getElementById('btn-entrar');

  errDiv.classList.add('oculto');
  btn.disabled = true; btn.textContent = 'Entrando...';

  try {
    const { token } = await Api.login(email, senha);
    const payload = JSON.parse(atob(token.split('.')[1]));
    Estado.salvar(token, email, payload.role);
    entrarNoApp();
  } catch (err) {
    errDiv.textContent = err.message || 'Credenciais inválidas.';
    errDiv.classList.remove('oculto');
  } finally {
    btn.disabled = false; btn.textContent = 'Entrar';
  }
});

function entrarNoApp() {
  document.getElementById('tela-login').classList.replace('ativa', 'oculto');
  document.getElementById('tela-app').classList.replace('oculto', 'ativa');
  document.getElementById('nav-nome-usuario').textContent = Estado.email;
  construirNavegacao(Estado.papel);
  construirDashboard(Estado.papel, Estado.email);
  mostrarPagina('pagina-dashboard');
  carregarReferencias();
}

async function carregarReferencias() {
  try {
    if (['SECRETARIA', 'DIRETOR', 'PROFESSOR', 'ALUNO'].includes(Estado.papel)) {
      Estado.turmas = await Api.listarTurmas();
      preencherSelectsTurmas();
    }
  } catch (ex) {
    console.warn('Nao foi possivel carregar listas auxiliares:', ex.message);
  }
}

function preencherSelectsAlunos() {
  [].forEach(id => {
    preencherSelect(id, Estado.alunos, aluno =>
      `${aluno.nome} - matricula ${aluno.matricula} (ID ${aluno.id})`
    );
  });
}

function preencherSelectsTurmas() {
  ['cad-aluno-turma-id', 'nota-turma-id', 'consulta-turma-id', 'av-turma-id'].forEach(id => {
    preencherSelect(id, Estado.turmas, turma =>
      `${turma.codigo} - ${turma.curso} (ID ${turma.id})`
    );
  });
}

function preencherSelect(id, itens, rotulo) {
  const select = document.getElementById(id);
  if (!select) return;
  const valorAtual = select.value;
  select.innerHTML = '<option value="">Selecione...</option>';
  itens.forEach(item => {
    const option = document.createElement('option');
    option.value = item.id;
    option.textContent = rotulo(item);
    select.appendChild(option);
  });
  if (valorAtual) select.value = valorAtual;
}

async function carregarAvaliacoesDaTurma(turmaId) {
  const select = document.getElementById('nota-avaliacao-id');
  if (!select || !turmaId) return;
  select.innerHTML = '<option value="">Carregando...</option>';
  try {
    const avaliacoes = await Api.listarAvaliacoes(turmaId);
    preencherSelect('nota-avaliacao-id', avaliacoes, avaliacao =>
      `${avaliacao.descricao} - ${avaliacao.turma} (ID ${avaliacao.id})`
    );
  } catch (ex) {
    select.innerHTML = '<option value="">Nenhuma avaliacao encontrada</option>';
  }
}

// =====================================================================
// Logout
// =====================================================================
document.getElementById('btn-sair').addEventListener('click', () => {
  Estado.limpar();
  document.getElementById('tela-app').classList.replace('ativa', 'oculto');
  document.getElementById('tela-login').classList.replace('oculto', 'ativa');
  document.getElementById('form-login').reset();
});

// =====================================================================
// UC-02 — Lançar Notas (Professor)
// =====================================================================
document.getElementById('av-tipo').addEventListener('change', (e) => {
  const tipo = e.target.value;
  const descricao = document.getElementById('av-descricao');
  const peso = document.getElementById('av-peso');
  if (tipo === 'N1') {
    descricao.value = 'N1';
    peso.value = '0.4';
  } else if (tipo === 'N2') {
    descricao.value = 'N2';
    peso.value = '0.7';
  } else if (tipo === 'TRABALHO') {
    descricao.value = 'Trabalho';
    peso.value = '1';
  }
});

document.getElementById('form-avaliacao').addEventListener('submit', async (e) => {
  e.preventDefault();
  const turmaId = Number(document.getElementById('av-turma-id').value);
  const dados = {
    descricao: document.getElementById('av-descricao').value.trim(),
    dataAplicacao: document.getElementById('av-data').value,
    peso: Number(document.getElementById('av-peso').value),
    valorMaximo: Number(document.getElementById('av-valor-maximo').value),
  };
  const err = document.getElementById('erro-avaliacao');
  const suc = document.getElementById('sucesso-avaliacao');
  err.classList.add('oculto'); suc.classList.add('oculto');
  try {
    const avaliacao = await Api.cadastrarAvaliacao(turmaId, dados);
    document.getElementById('nota-avaliacao-id').value = avaliacao.id;
    suc.textContent = `Avaliação #${avaliacao.id} cadastrada.`;
    suc.classList.remove('oculto');
    document.getElementById('form-avaliacao').reset();
    document.getElementById('av-turma-id').value = turmaId;
    await renderizarAvaliacoes(turmaId);
  } catch (ex) {
    err.textContent = ex.message; err.classList.remove('oculto');
  }
});

document.getElementById('btn-listar-avaliacoes').addEventListener('click', async () => {
  const turmaId = Number(document.getElementById('av-turma-id').value || document.getElementById('nota-turma-id').value);
  if (!turmaId) { alert('Selecione a turma.'); return; }
  await renderizarAvaliacoes(turmaId);
});

async function renderizarAvaliacoes(turmaId) {
  const avaliacoes = await Api.listarAvaliacoes(turmaId);
  const tbody = document.getElementById('tabela-avaliacoes-corpo');
  tbody.innerHTML = '';
  if (!avaliacoes.length) {
    tbody.innerHTML = '<tr><td colspan="4" style="text-align:center;color:#999">Nenhuma avaliação cadastrada.</td></tr>';
  } else {
    avaliacoes.forEach(a => {
      const tr = document.createElement('tr');
      tr.innerHTML = `<td>${a.id}</td><td>${a.descricao}</td><td>${a.turma}</td><td>${a.valorMaximo}</td>`;
      tbody.appendChild(tr);
    });
  }
  document.getElementById('tabela-avaliacoes-wrapper').classList.remove('oculto');
}

document.getElementById('form-nota').addEventListener('submit', async (e) => {
  e.preventDefault();
  const turmaId     = Number(document.getElementById('nota-turma-id').value);
  const alunoId     = Number(document.getElementById('nota-aluno-id').value);
  const avaliacaoId = Number(document.getElementById('nota-avaliacao-id').value);
  const valor       = Number(document.getElementById('nota-valor').value);
  const observacao  = document.getElementById('nota-observacao').value.trim() || null;
  const err = document.getElementById('erro-nota');
  const suc = document.getElementById('sucesso-nota');
  err.classList.add('oculto'); suc.classList.add('oculto');

  try {
    await Api.lancarNota(turmaId, alunoId, avaliacaoId, valor, observacao);
    suc.textContent = `Nota ${valor} lançada com sucesso!`;
    suc.classList.remove('oculto');
    document.getElementById('nota-aluno-id').value = '';
    document.getElementById('nota-avaliacao-id').value = '';
    document.getElementById('nota-valor').value = '';
    document.getElementById('nota-observacao').value = '';
    document.getElementById('nota-turma-id').value = turmaId;
  } catch (ex) {
    err.textContent = ex.message; err.classList.remove('oculto');
  }
});

document.getElementById('nota-turma-id').addEventListener('change', async (e) => {
  document.getElementById('nota-avaliacao-id').value = '';
  await carregarReferenciasDaTurma(e.target.value);
});

async function carregarReferenciasDaTurma(turmaId) {
  if (!turmaId) return;
  await Promise.all([
    renderizarAlunosDaTurma(turmaId),
    renderizarAvaliacoesDaTurma(turmaId),
  ]);
}

async function renderizarAlunosDaTurma(turmaId) {
  const alunos = await Api.listarAlunosDaTurma(turmaId);
  const tbody = document.getElementById('tabela-alunos-turma-corpo');
  tbody.innerHTML = '';
  if (!alunos.length) {
    tbody.innerHTML = '<tr><td colspan="3" style="text-align:center;color:#999">Nenhum aluno matriculado nesta turma.</td></tr>';
  } else {
    alunos.forEach(aluno => {
      const tr = document.createElement('tr');
      tr.innerHTML = `<td>${aluno.id}</td><td>${aluno.nome}</td><td>${aluno.matricula ?? '—'}</td>`;
      tbody.appendChild(tr);
    });
  }
  document.getElementById('tabela-alunos-turma-wrapper').classList.remove('oculto');
}

async function renderizarAvaliacoesDaTurma(turmaId) {
  const avaliacoes = await Api.listarAvaliacoes(turmaId);
  const tbody = document.getElementById('tabela-avaliacoes-turma-corpo');
  tbody.innerHTML = '';
  if (!avaliacoes.length) {
    tbody.innerHTML = '<tr><td colspan="3" style="text-align:center;color:#999">Nenhuma avaliação cadastrada nesta turma.</td></tr>';
  } else {
    avaliacoes.forEach(avaliacao => {
      const tr = document.createElement('tr');
      tr.innerHTML = `<td>${avaliacao.id}</td><td>${avaliacao.descricao}</td><td>${avaliacao.peso}</td>`;
      tbody.appendChild(tr);
    });
  }
  document.getElementById('tabela-avaliacoes-turma-wrapper').classList.remove('oculto');
}

document.getElementById('btn-consultar-notas').addEventListener('click', async () => {
  const turmaId = Number(document.getElementById('consulta-turma-id').value);
  const alunoId = Number(document.getElementById('consulta-aluno-id').value);
  if (!turmaId || !alunoId) { alert('Informe turma e aluno.'); return; }
  try {
    const notas = await Api.listarNotas(turmaId, alunoId);
    renderizarTabelaNotas('tabela-notas-corpo', notas);
    document.getElementById('tabela-notas-wrapper').classList.remove('oculto');
  } catch (ex) { alert('Erro: ' + ex.message); }
});

document.getElementById('btn-consultar-notas-turma').addEventListener('click', async () => {
  const turmaId = Number(document.getElementById('consulta-turma-id').value);
  if (!turmaId) { alert('Informe a turma.'); return; }
  try {
    const notas = await Api.listarNotasDaTurma(turmaId);
    renderizarTabelaNotas('tabela-notas-corpo', notas);
    document.getElementById('tabela-notas-wrapper').classList.remove('oculto');
  } catch (ex) { alert('Erro: ' + ex.message); }
});

// =====================================================================
// UC-05 — Frequência (Professor)
// =====================================================================
document.getElementById('form-frequencia').addEventListener('submit', async (e) => {
  e.preventDefault();
  const turmaId       = Number(document.getElementById('freq-turma-id').value);
  const alunoId       = Number(document.getElementById('freq-aluno-id').value);
  const data          = document.getElementById('freq-data').value;
  const presente      = document.getElementById('freq-presente').value === 'true';
  const justificativa = document.getElementById('freq-justificativa').value.trim() || null;
  const err = document.getElementById('erro-freq');
  const suc = document.getElementById('sucesso-freq');
  err.classList.add('oculto'); suc.classList.add('oculto');

  try {
    await Api.registrarFrequencia(turmaId, alunoId, data, presente, justificativa);
    suc.textContent = `Frequência registrada: aluno ${alunoId} — ${presente ? 'Presente' : 'Ausente'} em ${data}.`;
    suc.classList.remove('oculto');
    document.getElementById('form-frequencia').reset();
  } catch (ex) {
    err.textContent = ex.message; err.classList.remove('oculto');
  }
});

document.getElementById('btn-consultar-freq').addEventListener('click', async () => {
  const turmaId = Number(document.getElementById('freq-consulta-turma-id').value);
  const alunoId = Number(document.getElementById('freq-consulta-aluno-id').value);
  if (!turmaId || !alunoId) { alert('Informe turma e aluno.'); return; }
  try {
    const dados = await Api.consultarFrequencia(turmaId, alunoId);
    renderizarFrequencia(dados, 'freq-total', 'freq-presencas', 'freq-percentual', 'freq-situacao');
    document.getElementById('freq-resultado').classList.remove('oculto');
  } catch (ex) { alert('Erro: ' + ex.message); }
});

// =====================================================================
// Aluno — Minhas Notas
// =====================================================================
document.getElementById('btn-ver-minhas-notas').addEventListener('click', async () => {
  const turmaId = Number(document.getElementById('aluno-turma-id').value);
  if (!turmaId) { alert('Informe o ID da turma.'); return; }
  try {
    const alunoId = await Api.obterMeuId();
    const notas   = await Api.listarNotas(turmaId, alunoId);
    renderizarTabelaNotas('tabela-minhas-notas-corpo', notas);
    document.getElementById('tabela-minhas-notas-wrapper').classList.remove('oculto');
  } catch (ex) { alert('Erro: ' + ex.message); }
});

// =====================================================================
// Aluno — Minha Frequência
// =====================================================================
document.getElementById('btn-ver-minha-freq').addEventListener('click', async () => {
  const turmaId = Number(document.getElementById('aluno-freq-turma-id').value);
  if (!turmaId) { alert('Informe o ID da turma.'); return; }
  try {
    const alunoId = await Api.obterMeuId();
    const dados   = await Api.consultarFrequencia(turmaId, alunoId);
    renderizarFrequencia(dados, 'minha-freq-total', 'minha-freq-presencas', 'minha-freq-percentual', 'minha-freq-situacao');
    document.getElementById('minha-freq-resultado').classList.remove('oculto');
  } catch (ex) { alert('Erro: ' + ex.message); }
});

// =====================================================================
// UC-01 — Matrículas (Secretaria)
// =====================================================================
document.getElementById('form-turma').addEventListener('submit', async (e) => {
  e.preventDefault();
  const dados = {
    codigo: document.getElementById('cad-turma-codigo').value.trim(),
    anoLetivo: document.getElementById('cad-turma-ano').value.trim(),
    turno: document.getElementById('cad-turma-turno').value.trim(),
    curso: document.getElementById('cad-turma-curso').value.trim(),
    cargaHorariaTotal: Number(document.getElementById('cad-turma-carga').value),
  };
  const err = document.getElementById('erro-turma');
  const suc = document.getElementById('sucesso-turma');
  err.classList.add('oculto'); suc.classList.add('oculto');
  try {
    const turma = await Api.cadastrarTurma(dados);
    Estado.turmas = await Api.listarTurmas();
    preencherSelectsTurmas();
    document.getElementById('cad-aluno-turma-id').value = turma.id;
    suc.textContent = `Turma ${turma.codigo} cadastrada.`;
    suc.classList.remove('oculto');
    document.getElementById('form-turma').reset();
  } catch (ex) {
    err.textContent = ex.message; err.classList.remove('oculto');
  }
});

document.getElementById('form-cadastro-aluno').addEventListener('submit', async (e) => {
  e.preventDefault();
  const turmaId = Number(document.getElementById('cad-aluno-turma-id').value);
  const dados = {
    nome: document.getElementById('cad-aluno-nome').value.trim(),
    email: document.getElementById('cad-aluno-email').value.trim(),
    matricula: document.getElementById('cad-aluno-matricula').value.trim(),
    dataNascimento: document.getElementById('cad-aluno-nascimento').value,
    cpf: document.getElementById('cad-aluno-cpf').value.trim() || null,
  };
  const err = document.getElementById('erro-cadastro-aluno');
  const suc = document.getElementById('sucesso-cadastro-aluno');
  err.classList.add('oculto'); suc.classList.add('oculto');
  if (!turmaId) {
    err.textContent = 'Selecione a turma para matricular o aluno.';
    err.classList.remove('oculto');
    return;
  }

  try {
    const aluno = await Api.cadastrarAluno(dados);
    const matricula = await Api.matricular(aluno.id, turmaId);
    suc.textContent = `Aluno ${aluno.nome} cadastrado e matriculado. Matrícula #${matricula.id}. Senha inicial: Senha@123`;
    suc.classList.remove('oculto');
    document.getElementById('form-cadastro-aluno').reset();
  } catch (ex) {
    err.textContent = ex.message; err.classList.remove('oculto');
  }
});

document.getElementById('btn-cancelar-matricula').addEventListener('click', async () => {
  const id     = Number(document.getElementById('cancelar-matricula-id').value);
  const motivo = document.getElementById('cancelar-motivo').value.trim();
  const err = document.getElementById('erro-cancelamento');
  const suc = document.getElementById('sucesso-cancelamento');
  err.classList.add('oculto'); suc.classList.add('oculto');

  if (!id || !motivo) { alert('Informe ID e motivo.'); return; }
  try {
    await Api.cancelarMatricula(id, motivo);
    suc.textContent = `Matrícula #${id} cancelada.`;
    suc.classList.remove('oculto');
  } catch (ex) {
    err.textContent = ex.message; err.classList.remove('oculto');
  }
});

document.getElementById('btn-consultar-matriculas').addEventListener('click', async () => {
  const alunoId = Number(document.getElementById('consulta-mat-aluno-id').value);
  const turmaId = Number(document.getElementById('consulta-mat-turma-id').value);
  if (!alunoId) { alert('Informe o ID do aluno.'); return; }
  try {
    const lista = (await Api.listarMatriculas(alunoId))
      .filter(m => !turmaId || m.turma?.id === turmaId || m.turmaId === turmaId);
    const tbody = document.getElementById('tabela-matriculas-corpo');
    tbody.innerHTML = '';
    if (!lista.length) {
      tbody.innerHTML = '<tr><td colspan="4" style="text-align:center;color:#999">Nenhuma matrícula encontrada.</td></tr>';
    } else {
      lista.forEach(m => {
        const tr = document.createElement('tr');
        tr.innerHTML = `<td>${m.id}</td><td>${m.turma?.codigo ?? m.turmaId ?? '—'}</td><td><span class="badge badge-status-${m.status}">${m.status}</span></td><td>${m.dataEfetivacao ?? '—'}</td>`;
        tbody.appendChild(tr);
      });
    }
    document.getElementById('tabela-matriculas-wrapper').classList.remove('oculto');
  } catch (ex) { alert('Erro: ' + ex.message); }
});

document.getElementById('btn-listar-todas-matriculas').addEventListener('click', async () => {
  await renderizarTodasMatriculas();
});

async function renderizarTodasMatriculas() {
  try {
    const lista = (await Api.listarTodasMatriculas()).sort((a, b) => {
      const turma = String(a.turma ?? '').localeCompare(String(b.turma ?? ''), 'pt-BR', { sensitivity: 'base' });
      if (turma !== 0) return turma;
      return String(a.aluno ?? '').localeCompare(String(b.aluno ?? ''), 'pt-BR', { sensitivity: 'base' });
    });
    const tbody = document.getElementById('tabela-todas-matriculas-corpo');
    tbody.innerHTML = '';
    if (!lista.length) {
      tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;color:#999">Nenhum aluno matriculado.</td></tr>';
    } else {
      let turmaAtual = null;
      lista.forEach(m => {
        const turma = m.turma ?? 'Sem turma';
        if (turma !== turmaAtual) {
          turmaAtual = turma;
          const trTurma = document.createElement('tr');
          trTurma.className = 'linha-grupo-turma';
          trTurma.innerHTML = `<td colspan="7">Turma ${turmaAtual}</td>`;
          tbody.appendChild(trTurma);
        }
        const tr = document.createElement('tr');
        tr.innerHTML = `<td>${m.id}</td><td>${m.aluno ?? '—'}</td><td>${m.alunoId ?? '—'}</td><td>${m.turma ?? '—'}</td><td><span class="badge badge-status-${m.status}">${m.status}</span></td><td>${m.dataEfetivacao ?? '—'}</td><td><button class="btn-secundario btn-editar-aluno" data-aluno-id="${m.alunoId}">Editar</button></td>`;
        tbody.appendChild(tr);
      });
    }
    document.getElementById('tabela-todas-matriculas-wrapper').classList.remove('oculto');
  } catch (ex) {
    alert('Erro: ' + ex.message);
  }
}

document.getElementById('tabela-todas-matriculas-corpo').addEventListener('click', async (e) => {
  const botao = e.target.closest('.btn-editar-aluno');
  if (!botao) return;
  await carregarAlunoParaEdicao(Number(botao.dataset.alunoId));
});

async function carregarAlunoParaEdicao(alunoId) {
  const aluno = await Api.obterAluno(alunoId);
  document.getElementById('edit-aluno-id').value = aluno.id;
  document.getElementById('edit-aluno-nome').value = aluno.nome ?? '';
  document.getElementById('edit-aluno-email').value = aluno.email ?? '';
  document.getElementById('edit-aluno-matricula').value = aluno.matricula ?? '';
  document.getElementById('edit-aluno-nascimento').value = aluno.dataNascimento ?? '';
  document.getElementById('edit-aluno-cpf').value = aluno.cpf ?? '';
  document.getElementById('erro-editar-aluno').classList.add('oculto');
  document.getElementById('sucesso-editar-aluno').classList.add('oculto');
  document.getElementById('card-editar-aluno').classList.remove('oculto');
  document.getElementById('card-editar-aluno').scrollIntoView({ behavior: 'smooth', block: 'start' });
}

document.getElementById('form-editar-aluno').addEventListener('submit', async (e) => {
  e.preventDefault();
  const alunoId = Number(document.getElementById('edit-aluno-id').value);
  const dados = {
    nome: document.getElementById('edit-aluno-nome').value.trim(),
    email: document.getElementById('edit-aluno-email').value.trim(),
    matricula: document.getElementById('edit-aluno-matricula').value.trim(),
    dataNascimento: document.getElementById('edit-aluno-nascimento').value,
    cpf: document.getElementById('edit-aluno-cpf').value.trim() || null,
  };
  const err = document.getElementById('erro-editar-aluno');
  const suc = document.getElementById('sucesso-editar-aluno');
  err.classList.add('oculto'); suc.classList.add('oculto');
  try {
    const aluno = await Api.atualizarAluno(alunoId, dados);
    suc.textContent = `Dados de ${aluno.nome} atualizados.`;
    suc.classList.remove('oculto');
    await renderizarTodasMatriculas();
  } catch (ex) {
    err.textContent = ex.message; err.classList.remove('oculto');
  }
});

// =====================================================================
// UC-04 — Documentos (Secretaria / Aluno)
// =====================================================================
document.getElementById('btn-gerar-boletim').addEventListener('click', async () => {
  const alunoId = Number(document.getElementById('bol-aluno-id').value);
  const turmaId = Number(document.getElementById('bol-turma-id').value);
  const periodo = document.getElementById('bol-periodo').value.trim();
  const err = document.getElementById('erro-boletim');
  err.classList.add('oculto');
  if (!alunoId || !turmaId || !periodo) { alert('Preencha todos os campos.'); return; }
  try {
    const blob = await Api.gerarBoletim(alunoId, turmaId, periodo);
    baixarBlob(blob, `boletim_${alunoId}_${periodo}.pdf`);
  } catch (ex) { err.textContent = ex.message; err.classList.remove('oculto'); }
});

document.getElementById('btn-gerar-historico').addEventListener('click', async () => {
  const alunoId = Number(document.getElementById('hist-aluno-id').value);
  const err = document.getElementById('erro-historico');
  err.classList.add('oculto');
  if (!alunoId) { alert('Informe o ID do aluno.'); return; }
  try {
    const blob = await Api.gerarHistorico(alunoId);
    baixarBlob(blob, `historico_${alunoId}.pdf`);
  } catch (ex) { err.textContent = ex.message; err.classList.remove('oculto'); }
});

// =====================================================================
// Helpers
// =====================================================================
function renderizarTabelaNotas(tbodyId, notas) {
  const tbody = document.getElementById(tbodyId);
  tbody.innerHTML = '';
  if (!notas || !notas.length) {
    tbody.innerHTML = '<tr><td colspan="6" style="text-align:center;color:#999">Nenhuma nota encontrada.</td></tr>';
    return;
  }

  const grupos = new Map();
  notas.forEach(n => {
    const chave = n.alunoId ?? n.aluno ?? 'aluno';
    if (!grupos.has(chave)) grupos.set(chave, []);
    grupos.get(chave).push(n);
  });

  grupos.forEach(grupo => {
    grupo.forEach(n => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${n.aluno ?? n.aluno?.nome ?? '—'}</td>
        <td>${n.avaliacao?.descricao ?? n.avaliacao ?? '—'}</td>
        <td>${n.peso ?? '—'}</td>
        <td><strong>${n.valor}</strong></td>
        <td>${n.observacao ?? '—'}</td>
        <td>${n.lancadaEm ? new Date(n.lancadaEm).toLocaleDateString('pt-BR') : '—'}</td>`;
      tbody.appendChild(tr);
    });

    const resumo = grupo[0];
    const trResumo = document.createElement('tr');
    trResumo.className = 'linha-resumo-notas';
    trResumo.innerHTML = `
      <td colspan="3"><strong>Situação de ${resumo.aluno ?? 'aluno'}</strong></td>
      <td colspan="3"><strong>Média ${resumo.mediaFinal ?? '—'}</strong> — <span class="badge badge-situacao-${resumo.situacao}">${formatarSituacao(resumo.situacao)}</span></td>`;
    tbody.appendChild(trResumo);
  });
}

function formatarSituacao(situacao) {
  if (situacao === 'APROVADO') return 'Aprovado';
  if (situacao === 'RECUPERACAO') return 'Recuperação';
  if (situacao === 'REPROVADO') return 'Reprovado';
  return '—';
}

function renderizarFrequencia(dados, idTotal, idPresencas, idPercentual, idSituacao) {
  const total     = dados.totalAulas ?? 0;
  const presencas = dados.presencas  ?? 0;
  const pct       = total > 0 ? ((presencas / total) * 100).toFixed(1) : '0.0';
  const aprovado  = parseFloat(pct) >= 75;

  document.getElementById(idTotal).textContent     = total;
  document.getElementById(idPresencas).textContent  = presencas;
  document.getElementById(idPercentual).textContent = pct + '%';

  const sit = document.getElementById(idSituacao);
  sit.textContent  = aprovado ? 'Regular' : 'Risco de Reprovação';
  sit.style.color  = aprovado ? 'var(--verde)' : 'var(--perigo)';
}

function baixarBlob(blob, nome) {
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url; a.download = nome; a.click();
  URL.revokeObjectURL(url);
}

// =====================================================================
// Restaurar sessão ao recarregar a página
// =====================================================================
(function init() {
  if (Estado.restaurar()) entrarNoApp();
})();
